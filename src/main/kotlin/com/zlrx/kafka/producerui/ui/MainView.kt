package com.zlrx.kafka.producerui.ui

import com.vaadin.flow.component.Text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InitialPageSettings
import com.vaadin.flow.server.PageConfigurator
import com.zlrx.kafka.producerui.domain.Configuration
import com.zlrx.kafka.producerui.domain.Connection
import com.zlrx.kafka.producerui.domain.Header
import com.zlrx.kafka.producerui.domain.Message
import com.zlrx.kafka.producerui.domain.Topic
import com.zlrx.kafka.producerui.message.FileData
import com.zlrx.kafka.producerui.message.FilePath
import com.zlrx.kafka.producerui.message.MessageData
import com.zlrx.kafka.producerui.message.ProducerProps
import com.zlrx.kafka.producerui.service.FileHandlerService
import com.zlrx.kafka.producerui.service.KafkaService
import com.zlrx.kafka.producerui.service.ProducerService
import com.zlrx.kafka.producerui.ui.component.ArrowText
import com.zlrx.kafka.producerui.ui.component.Divider
import org.springframework.beans.factory.annotation.Autowired

enum class SAVE_TYPE {
    UPDATE, SAVE
}

@Route
class MainView @Autowired constructor(
    private val kafkaService: KafkaService,
    private val producerService: ProducerService,
    private val fileHandlerService: FileHandlerService
) : HorizontalLayout(), PageConfigurator {

    private val propertyLayout = VerticalLayout()
    private val messageLayout = VerticalLayout()
    private val messageBtnLayout = HorizontalLayout()

    private val connectionSelect = Select<Connection>()

    private val topicSelect = Select<Topic>()
    private val keyTxtField = TextField()

    private val headerTypeTxt = TextField()
    private val headerValueTxt = TextField()
    private val addToHeadersButton = Button("Add", Icon(VaadinIcon.PLUS_CIRCLE))
    private val headerGrid = Grid<Header>(Header::class.java)

    private val fileComboBox = ComboBox<FilePath>()
    private val messageTxtArea = TextArea()
    private val messageTypeSelector = RadioButtonGroup<String>()
    private val sendBtn = Button("Send", Icon(VaadinIcon.BOLT))
    private val saveBtn = Button("Save", Icon(VaadinIcon.CHECK_CIRCLE))
    private val saveAsNewBtn = Button("Save as new", Icon(VaadinIcon.FOLDER_ADD))
    private val addConnectionBtn = Button(Icon(VaadinIcon.PLUS_CIRCLE))
    private val addTopicBtn = Button(Icon(VaadinIcon.PLUS_CIRCLE))
    private val fileLayout = VerticalLayout()

    private val connectionDialog: Dialog = Dialog()
    private val topicDialog: Dialog = Dialog()

    private var configuration: Configuration

    private var newConfigurationNameTxt = TextField()

    init {
        configuration = producerService.loadDefaultConfiguration()
        setSizeFull()
        propertyLayout.setHeightFull()
        propertyLayout.width = "45%"
        messageLayout.setSizeFull()
        messageBtnLayout.setWidthFull()
        this.add(propertyLayout, messageLayout)
        registerKafkaLayout()
        registerTopicLayout()
        registerHeaderLayout()
        registerHeaderGrid()
        registerMessageTypeSelector()
        registerFilesComboBox()
        registerJsonMessageArea()
        registerSendBtn()
        registerNewButton()
        registerConnectionDialog()
        registerTopicDialog()
        loadDefaultConfiguration()
        messageLayout.add(messageBtnLayout)
    }

    private fun loadDefaultConfiguration() {
        configuration.let {
            connectionSelect.value = it.connection
            topicSelect.value = it.topic
            keyTxtField.value = it.message?.key ?: ""
            val isFile = it.message?.file ?: false
            messageTypeSelector.value = if (isFile) "File" else "Text"
            if (isFile) {
                fileComboBox.value = FilePath(it.message?.fileName!!, it.message?.filePath!!)
            } else {
                messageTxtArea.value = it.message?.text ?: ""
            }
        }
    }

    private fun saveConfiguration(saveType: SAVE_TYPE) {
        configuration.connection = connectionSelect.value
        configuration.topic = topicSelect.value
        buildMessage(configuration.message)
        if (saveType == SAVE_TYPE.SAVE) {
            configuration = configuration.copy(newConfigurationNameTxt.value)
        }
        configuration = producerService.saveConfiguration(configuration)
        Notification.show("Saved", 2000, Notification.Position.MIDDLE)
        loadConfiguration(configuration)
    }

    private fun buildMessage(message: Message) {
        message.file = messageTypeSelector.value == "File"
        message.fileName = fileComboBox.value?.name
        message.filePath = fileComboBox.value?.path
        message.key = keyTxtField.value
        message.text = messageTxtArea.value
    }

    private fun loadConfiguration(configuration: Configuration) {
        headerGrid.setItems(configuration.message.headers)
        headerGrid.dataProvider.refreshAll()
        loadDefaultConfiguration()
    }

    private fun registerFilesComboBox() {
        val refreshButton = Button(Icon(VaadinIcon.REFRESH))
        refreshButton.addClickListener {
            fileComboBox.setItems(fileHandlerService.readFilesFromDir())
        }
        fileLayout.isVisible = false
        fileLayout.width = "60%"
        fileComboBox.setItems(fileHandlerService.readFilesFromDir())
        fileComboBox.width = "50%"
        fileComboBox.setItemLabelGenerator { it.name }
        val comboLayout = HorizontalLayout()
        comboLayout.setSizeFull()
        comboLayout.add(fileComboBox, refreshButton)
        comboLayout.isMargin = false
        val text = Text("The file content will be sent line by line!")
        fileLayout.add(text, comboLayout)
        messageLayout.add(fileLayout)
    }

    private fun registerMessageTypeSelector() {
        messageTypeSelector.setItems("Text", "File")
        messageTypeSelector.value = "Text"
        messageTypeSelector.addValueChangeListener {
            if (messageTypeSelector.value == "Text") {
                messageTxtArea.isVisible = true
                fileLayout.isVisible = false
            } else {
                messageTxtArea.isVisible = false
                fileLayout.isVisible = true
            }
        }
        messageLayout.add(messageTypeSelector)
    }

    private fun registerTopicDialog() {
        topicDialog.isCloseOnOutsideClick = false
        topicDialog.isCloseOnEsc = true
        topicDialog.width = "450px"
        val layout = VerticalLayout()
        layout.setSizeFull()
        val nameTxt = TextField()
        val topicTxt = TextField()
        nameTxt.setSizeFull()
        topicTxt.setSizeFull()
        nameTxt.label = "Visible name"
        topicTxt.label = "Topic name"

        val buttonLayout = HorizontalLayout()
        val closeBtn = Button("Close")
        val saveBtn = Button("Save")
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY)

        closeBtn.addClickListener { topicDialog.close() }
        saveBtn.addClickListener {
            //TODO validate
            val newTopic = producerService.saveTopic(nameTxt.value, topicTxt.value)
            topicSelect.setItems(producerService.loadTopics())
            topicSelect.value = newTopic
            nameTxt.value = ""
            topicTxt.value = ""
            topicDialog.close()
        }
        buttonLayout.add(saveBtn, closeBtn)
        layout.add(nameTxt, topicTxt)
        topicDialog.add(layout, buttonLayout)
    }

    private fun registerConnectionDialog() {
        connectionDialog.isCloseOnEsc = true
        connectionDialog.isCloseOnOutsideClick = false
        connectionDialog.width = "450px"
        val layout = VerticalLayout()
        layout.setSizeFull()
        val nameTxt = TextField()
        val brokerTxt = TextField()
        val schemaTxt = TextField()
        nameTxt.setSizeFull()
        brokerTxt.setSizeFull()
        schemaTxt.setSizeFull()
        nameTxt.label = "Name"
        brokerTxt.label = "Kafka broker"
        schemaTxt.label = "Schema registry"

        val buttonLayout = HorizontalLayout()
        val closeBtn = Button("Close")
        val saveBtn = Button("Save")
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY)

        closeBtn.addClickListener { connectionDialog.close() }
        saveBtn.addClickListener {
            //TODO validate
            val newConnection = producerService.saveConnection(nameTxt.value, brokerTxt.value, schemaTxt.value)
            connectionSelect.setItems(producerService.loadConnections())
            connectionSelect.value = newConnection
            nameTxt.value = ""
            brokerTxt.value = ""
            schemaTxt.value = ""
            connectionDialog.close()
        }
        buttonLayout.add(saveBtn, closeBtn)
        layout.add(nameTxt, brokerTxt, schemaTxt)
        connectionDialog.add(layout, buttonLayout)
    }

    private fun registerKafkaLayout() {
        val layout = VerticalLayout()
        layout.setWidthFull()
        val kafkaDiv = ArrowText("Broker", "Not selected")
        val registryDiv = ArrowText("Registry", "Not selected")
        val connectionSelectLayout = HorizontalLayout()
        connectionSelectLayout.setSizeFull()
        connectionSelectLayout.alignItems = FlexComponent.Alignment.BASELINE
        connectionSelect.label = "Connection"
        connectionSelect.setSizeFull()
        connectionSelect.setItems(producerService.loadConnections())
        connectionSelect.setTextRenderer { it.name }
        connectionSelect.addValueChangeListener {
            if (it.value != null) {
                kafkaDiv.text = "Broker: ${it.value.broker}"
                registryDiv.text = "Registry: ${it.value.schemaRegistry}"
            }
        }
        addConnectionBtn.addClickListener { connectionDialog.open() }
        addConnectionBtn.style.set("minHeight", "35px")
        connectionSelectLayout.add(connectionSelect, addConnectionBtn)
        layout.add(connectionSelectLayout, kafkaDiv, registryDiv)
        propertyLayout.add(layout, Divider())
    }

    private fun registerTopicLayout() {
        val topicLayout = VerticalLayout()
        topicLayout.setWidthFull()

        val topicSelectLayout = HorizontalLayout()
        topicSelectLayout.setSizeFull()
        topicSelect.label = "Topic name"
        topicSelect.setWidthFull()
        topicSelect.setTextRenderer { it.name }
        topicSelect.setItems(producerService.loadTopics())
        addTopicBtn.addClickListener { topicDialog.open() }
        addTopicBtn.style.set("minHeight", "35px")

        topicSelectLayout.add(topicSelect, addTopicBtn)
        topicSelectLayout.alignItems = FlexComponent.Alignment.BASELINE

        keyTxtField.label = "Key (optional)"
        keyTxtField.setWidthFull()
        topicLayout.add(topicSelectLayout, keyTxtField)
        propertyLayout.add(topicLayout, Divider())
    }

    private fun registerHeaderLayout() {
        val headerLayout = VerticalLayout()
        headerLayout.setWidthFull()
        headerTypeTxt.label = "Header key";
        headerTypeTxt.value = ""
        headerTypeTxt.setWidthFull()
        headerValueTxt.label = "Header value"
        headerValueTxt.value = ""
        headerValueTxt.setWidthFull()
        registerAddToHeaderBtnClickListener()
        addToHeadersButton.style.set("minHeight", "35px")
        headerLayout.add(headerTypeTxt, headerValueTxt, addToHeadersButton)
        propertyLayout.add(headerLayout)
    }

    private fun registerHeaderGrid() {
        headerGrid.setItems(configuration.message.headers)
        headerGrid.pageSize = 4
        headerGrid.setSizeFull()
        headerGrid.setColumns("key", "value")
        headerGrid.style.set("maxHeight", "300px")

        val contextMenu = GridContextMenu<Header>(headerGrid)
        contextMenu.addItem("Remove") {
            it.item.ifPresent { h ->
                configuration.message.headers.remove(h)
                headerGrid.dataProvider.refreshAll()
            }
        }
        propertyLayout.add(headerGrid)
    }

    private fun registerJsonMessageArea() {
        messageTxtArea.label = "Message JSON"
        messageTxtArea.style.set("minHeight", "500px")
        messageTxtArea.setSizeFull()
        messageLayout.add(messageTxtArea)
    }

    private fun registerNewButton() {
        val saveDialog = Dialog()
        saveDialog.isCloseOnEsc = true
        saveDialog.isCloseOnOutsideClick = false
        saveDialog.width = "400px"
        newConfigurationNameTxt.setWidthFull()
        newConfigurationNameTxt.label = "Name"
        val layout = VerticalLayout()
        layout.setSizeFull()
        val buttonLayout = HorizontalLayout()
        val closeBtn = Button("Close")
        val saveNewBtn = Button("Save")
        saveNewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        closeBtn.addClickListener { saveDialog.close() }
        saveNewBtn.addClickListener {
            saveConfiguration(SAVE_TYPE.SAVE)
            newConfigurationNameTxt.value = ""
            saveDialog.close()
        }
        buttonLayout.add(saveNewBtn, closeBtn)
        layout.add(newConfigurationNameTxt)
        saveDialog.add(layout, buttonLayout)

        saveBtn.style.set("minHeight", "40px")
        saveBtn.addClickListener {
            saveConfiguration(SAVE_TYPE.UPDATE)
        }
        saveAsNewBtn.style.set("minHeight", "40px")
        saveAsNewBtn.addClickListener {
            saveDialog.open()
        }
        messageBtnLayout.add(saveBtn, saveAsNewBtn)
    }

    private fun registerSendBtn() {
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        sendBtn.style.set("minHeight", "40px")
        sendBtn.addClickListener {
            //TODO validate
            try {
                if (messageTypeSelector.value == "Text") {
                    kafkaService.sendMessage(MessageData(
                        topicSelect.value.topicName,
                        keyTxtField.value,
                        messageTxtArea.value,
                        configuration.message.headers,
                        ProducerProps(
                            connectionSelect.value.broker,
                            connectionSelect.value.schemaRegistry
                        )
                    ))
                    Notification.show("Message was sent into the given topic!", 2000, Notification.Position.MIDDLE)
                    cleanUp()
                } else {
                    //TODO validate
                    Notification.show("Messages from file was started to send into the given topic!", 1500, Notification.Position.MIDDLE)
                    kafkaService.sendMessageFromFile(
                        FileData(
                            topicSelect.value.topicName,
                            keyTxtField.value,
                            fileComboBox.value,
                            configuration.message.headers,
                            ProducerProps(
                                connectionSelect.value.broker,
                                connectionSelect.value.schemaRegistry
                            )
                        )
                    )
                    Notification.show("All message has been processed from the file, it may take time while it will be written to the given topic", 3000, Notification.Position.MIDDLE)
                }
            } catch (e: Exception) {
                Notification.show("Failed to send", 3000, Notification.Position.MIDDLE)
            }
        }
        messageBtnLayout.add(sendBtn)
    }

    private fun registerAddToHeaderBtnClickListener() {
        addToHeadersButton.addClickListener {
            val key = headerTypeTxt.value
            val value = headerValueTxt.value
            if (!value.isBlank() && !key.isBlank()) {
                configuration.message.headers.add(Header(key, value, configuration.message))
                headerGrid.dataProvider.refreshAll()
                headerTypeTxt.value = ""
                headerValueTxt.value = ""
            } else {
                Notification.show("Header key and value fields are required!", 2000, Notification.Position.MIDDLE)
            }
        }
    }

    fun cleanUp() {
        //TODO clean up field after message was sent, savable property to clean up
    }

    override fun configurePage(settings: InitialPageSettings?) {
        settings?.loadingIndicatorConfiguration?.isApplyDefaultTheme = false
    }

}