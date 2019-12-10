package com.zlrx.kafka.producerui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.radiobutton.RadioButtonGroup
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InitialPageSettings
import com.vaadin.flow.server.PageConfigurator
import com.zlrx.kafka.producerui.ProducerService
import com.zlrx.kafka.producerui.domain.Connection
import com.zlrx.kafka.producerui.domain.Header
import com.zlrx.kafka.producerui.message.FileData
import com.zlrx.kafka.producerui.message.FilePath
import com.zlrx.kafka.producerui.message.MessageData
import com.zlrx.kafka.producerui.message.ProducerProps
import com.zlrx.kafka.producerui.service.FileHandlerService
import com.zlrx.kafka.producerui.service.KafkaService
import com.zlrx.kafka.producerui.ui.component.ArrowText
import com.zlrx.kafka.producerui.ui.component.Divider
import org.springframework.beans.factory.annotation.Autowired

//v2 -> register kafka, pick from select
//v2 -> register topic with header-> pick
@Route
class MainView @Autowired constructor(
    private val kafkaService: KafkaService,
    private val producerService: ProducerService,
    private val fileHandlerService: FileHandlerService
) : HorizontalLayout(), PageConfigurator {

    private val propertyLayout = VerticalLayout()
    private val messageLayout = VerticalLayout()

    private val connectionSelect = Select<Connection>()

    private val topicTxtField = TextField()
    private val keyTxtField = TextField()

    private val headerTypeTxt = TextField()
    private val headerValueTxt = TextField()
    private val addToHeadersButton = Button("Add", Icon(VaadinIcon.PLUS_CIRCLE))
    private val headerGrid = Grid<Header>(Header::class.java)

    private val fileComboBox = ComboBox<FilePath>()
    private val messageTxtArea = TextArea()
    private val messageTypeSelector = RadioButtonGroup<String>()
    private val sendBtn = Button("Send", Icon(VaadinIcon.BOLT))
    private val addConnectionBtn = Button("New", Icon(VaadinIcon.PLUS_CIRCLE))
    val fileLayout = HorizontalLayout()
    private val headers = mutableListOf<Header>()

    private val connectionDialog: Dialog = Dialog()

    init {
        setSizeFull()
        propertyLayout.setHeightFull()
        propertyLayout.width = "45%"
        messageLayout.setSizeFull()
        this.add(propertyLayout, messageLayout)
        registerKafkaLayout()
        registerTopicLayout()
        registerHeaderLayout()
        registerHeaderGrid()
        registerMessageTypeSelector()
        registerFilesComboBox()
        registerJsonMessageArea()
        registerSendBtn()
        registerConnectionDialog()

    }

    private fun loadDefaultConfiguration() {
        //TODO
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
        fileLayout.add(fileComboBox, refreshButton)
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

        layout.add(connectionSelect, kafkaDiv, registryDiv, addConnectionBtn)
        propertyLayout.add(layout, Divider())
    }

    private fun registerTopicLayout() {
        val topicLayout = VerticalLayout()
        topicLayout.setWidthFull()
        topicTxtField.label = "Topic name"
        topicTxtField.placeholder = "Auto create if not exists"
        topicTxtField.setWidthFull()
        keyTxtField.label = "Key (optional)"
        keyTxtField.setWidthFull()
        topicLayout.add(topicTxtField, keyTxtField)
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
        headerGrid.setItems(headers)
        headerGrid.pageSize = 4
        headerGrid.setSizeFull()
        headerGrid.setColumns("key", "value")
        headerGrid.style.set("maxHeight", "300px")

        val contextMenu = GridContextMenu<Header>(headerGrid)
        contextMenu.addItem("Remove") {
            it.item.ifPresent { h ->
                headers.remove(h)
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

    private fun registerSendBtn() {
        sendBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        sendBtn.style.set("minHeight", "40px")
        sendBtn.addClickListener {
            //TODO validate
            if (messageTypeSelector.value == "Text") {
                kafkaService.sendMessage(MessageData(
                    topicTxtField.value,
                    keyTxtField.value,
                    messageTxtArea.value,
                    headers,
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
                        topicTxtField.value,
                        keyTxtField.value,
                        fileComboBox.value,
                        headers,
                        ProducerProps(
                            connectionSelect.value.broker,
                            connectionSelect.value.schemaRegistry
                        )
                    )
                )
                Notification.show("All message has been processed from the file, it may take time while it will be written to the given topic", 3000, Notification.Position.MIDDLE)
            }
        }
        messageLayout.add(sendBtn)
    }

    private fun registerAddToHeaderBtnClickListener() {
        addToHeadersButton.addClickListener {
            val key = headerTypeTxt.value
            val value = headerValueTxt.value
            if (!value.isBlank() && !key.isBlank()) {
                headers.add(Header(key, value))
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