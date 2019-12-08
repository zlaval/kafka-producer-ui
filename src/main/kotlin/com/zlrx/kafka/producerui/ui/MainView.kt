package com.zlrx.kafka.producerui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.zlrx.kafka.producerui.ProducerService
import com.zlrx.kafka.producerui.domain.Connection
import com.zlrx.kafka.producerui.message.Header
import com.zlrx.kafka.producerui.message.MessageData
import com.zlrx.kafka.producerui.message.ProducerProps
import com.zlrx.kafka.producerui.service.KafkaService
import com.zlrx.kafka.producerui.ui.component.Divider
import org.springframework.beans.factory.annotation.Autowired

//v2 -> register kafka, pick from select
//v2 -> register topic with header-> pick
@Route
class MainView @Autowired constructor(
    private val kafkaService: KafkaService,
    private val producerService: ProducerService
) : HorizontalLayout() {

    private val propertyLayout = VerticalLayout()
    private val messageLayout = VerticalLayout()

    private val connectionSelect = Select<Connection>()

    private val topicTxtField = TextField()
    private val keyTxtField = TextField()

    private val headerTypeTxt = TextField()
    private val headerValueTxt = TextField()
    private val addToHeadersButton = Button("Add", Icon(VaadinIcon.PLUS_CIRCLE))
    private val headerGrid = Grid<Header>(Header::class.java)

    private val messageTxtArea = TextArea()
    private val sendBtn = Button("Send", Icon(VaadinIcon.BOLT))
    private val addConnectionBtn = Button("New", Icon(VaadinIcon.PLUS_CIRCLE))

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
        registerJsonMessageArea()
        registerSendBtn()
        registerConnectionDialog()
    }

    private fun registerConnectionDialog() {
        connectionDialog.isCloseOnEsc = true
        connectionDialog.isCloseOnOutsideClick = false

    }

    private fun registerKafkaLayout() {
        val layout = VerticalLayout()
        layout.setWidthFull()

        val kafkaDiv = Div()
        kafkaDiv.text = "Broker: not selected"
        val registryDiv = Div()
        registryDiv.text = "Registry: not selected"

        connectionSelect.label = "Connection"
        connectionSelect.setSizeFull()
        connectionSelect.setItems(producerService.loadConnections())
        connectionSelect.setTextRenderer { it.name }
        connectionSelect.addValueChangeListener {
            kafkaDiv.text = "Broker: ${it.value.broker}"
            registryDiv.text = "Registry: ${it.value.schemaRegistry}"
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

}