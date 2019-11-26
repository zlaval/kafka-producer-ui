package com.zlrx.kafka.producerui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import com.zlrx.kafka.producerui.message.Header
import com.zlrx.kafka.producerui.message.MessageData
import com.zlrx.kafka.producerui.message.ProducerProps
import com.zlrx.kafka.producerui.service.KafkaService
import org.springframework.beans.factory.annotation.Autowired

@Route
class MainView @Autowired constructor(kafkaService: KafkaService) : VerticalLayout() {

    private val topicTxtField = TextField()
    private val keyTxtField = TextField()
    private val messageTxtArea = TextArea()
    private val sendBtn = Button("Send", Icon(VaadinIcon.BOLT))

    private val headerTypeTxt = TextField()
    private val headerValueTxt = TextField()
    private val addToHeadersButton = Button("Add", Icon(VaadinIcon.PLUS_CIRCLE))

    private val headers = mutableListOf<Header>()

    init {
        val propertyLayout = HorizontalLayout()
        propertyLayout.setSizeFull()
        val topicLayout = VerticalLayout()
        val headerLayout = VerticalLayout()
        headerLayout.setSizeFull()


        topicTxtField.label = "Topic name"
        topicTxtField.placeholder = "will be created if not exists"

        keyTxtField.label = "Key (optional)"
        topicLayout.add(topicTxtField, keyTxtField)


        headerTypeTxt.label = "Header key";
        headerValueTxt.label = "Header value"
        val headerItemLayout = HorizontalLayout()
        headerItemLayout.setSizeFull()
        headerItemLayout.add(headerTypeTxt, headerValueTxt)

        val grid = Grid<Header>(Header::class.java)
        grid.setItems(headers)
        grid.pageSize = 5
        grid.setSizeFull()
        grid.setColumns("key", "value")

        addToHeadersButton.addClickListener {
            val key = headerTypeTxt.value
            val value = headerValueTxt.value
            if (value != null && key != null && !value.isBlank() && !key.isBlank()) {
                headers.add(Header(key, value))
                grid.dataProvider.refreshAll()
                headerTypeTxt.value = ""
                headerValueTxt.value = ""
            } else {
                Notification.show("Header key and value field is required!", 3000, Notification.Position.MIDDLE)
            }
        }

        headerLayout.add(headerItemLayout, addToHeadersButton)
        propertyLayout.add(topicLayout, headerLayout, grid)

        messageTxtArea.label = "Message JSON"
        messageTxtArea.style.set("minHeight", "500px")
        //messageTxtArea.style.set("minWidth", "100%")
        messageTxtArea.setSizeFull()

        sendBtn.addClickListener {
            kafkaService.sendMessage(MessageData(
                topicTxtField.value,
                keyTxtField.value,
                messageTxtArea.value,
                headers,
                ProducerProps(
                    "localhost:29092", "http://localhost:8081"
                )

            ))
        }

        this.add(propertyLayout, messageTxtArea, sendBtn)

    }


}