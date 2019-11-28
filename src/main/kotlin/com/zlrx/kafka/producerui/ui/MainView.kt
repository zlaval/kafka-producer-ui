package com.zlrx.kafka.producerui.ui

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
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

/**
 *
 * *********************VERTICAL_LAYOUT**************************
 *    KAFKA      *   TOPIC    *  HEADER HORIZ  * _______________*
 *  HORIZONTAL   * HORIZONTAL *+++ITEM_VERT++++*|    HEADER    |*
 *  _____________*   _______  *+ ____  ______ +*|     GRID     |*
 * |KAFKA BROKER|*  |TOPIC |  *+|KEY| |VALUE| +*|              |*
 * ------------- *  -------   *+----  ------  +*|              |*
 * ______________*  _______   *++++++++++++++++*|              |*
 *|SCHEMAREG URL|* | KEY  |   *  ________      * ---------------*
 * ------------- * -------    * |ADD BTN|      *                *
 *               *            * --------       *                *
 * **************************************************************
 *  __________________________________________________________  *
 * |                        JSON TEXTAREA                     | *
 * |                                                          | *
 * | _________________________________________________________| *
 *   _________                                                  *
 *  |SEND BTN|                                                  *
 *  ---------                                                   *
 ****************************************************************
 */
//v2 -> register kafka, pick from select
//v2 -> register topic with header-> pick
@Route
class MainView @Autowired constructor(private val kafkaService: KafkaService) : VerticalLayout() {

    private val propertyLayout = HorizontalLayout()

    private val brokerTxtField = TextField()
    private val registryTextField = TextField()

    private val topicTxtField = TextField()
    private val keyTxtField = TextField()

    private val headerTypeTxt = TextField()
    private val headerValueTxt = TextField()
    private val addToHeadersButton = Button("Add", Icon(VaadinIcon.PLUS_CIRCLE))
    private val headerGrid = Grid<Header>(Header::class.java)

    private val messageTxtArea = TextArea()
    private val sendBtn = Button("Send", Icon(VaadinIcon.BOLT))

    private val headers = mutableListOf<Header>()

    init {
        setSizeFull()
        propertyLayout.setWidthFull()
        this.add(propertyLayout)
        registerKafkaLayout()
        registerTopicLayout()
        registerHeaderLayout()
        registerHeaderGrid()
        registerJsonMessageArea()
        registerSendBtn()
    }

    private fun registerKafkaLayout() {
        val layout = VerticalLayout()
        layout.setSizeFull()
        brokerTxtField.label = "Kafka broker"
        brokerTxtField.value = "kafka:29092"
        brokerTxtField.setWidthFull()
        registryTextField.label = "Schema registry url"
        registryTextField.value = "http://schema-registry:8081"
        registryTextField.setWidthFull()
        layout.add(brokerTxtField, registryTextField)
        propertyLayout.add(layout)
    }

    private fun registerTopicLayout() {
        val topicLayout = VerticalLayout()
        topicLayout.setSizeFull()
        topicTxtField.label = "Topic name"
        topicTxtField.placeholder = "Auto create if not exists"
        topicTxtField.setWidthFull()
        keyTxtField.label = "Key (optional)"
        keyTxtField.setWidthFull()
        topicLayout.add(topicTxtField, keyTxtField)
        propertyLayout.add(topicLayout)
    }

    private fun registerHeaderLayout() {
        val headerLayout = VerticalLayout()
        headerLayout.setWidthFull()
        val headerItemLayout = HorizontalLayout()
        headerItemLayout.setSizeFull()
        headerTypeTxt.label = "Header key";
        headerTypeTxt.value = ""
        headerTypeTxt.setWidthFull()
        headerValueTxt.label = "Header value"
        headerValueTxt.value = ""
        headerValueTxt.setWidthFull()
        headerItemLayout.add(headerTypeTxt, headerValueTxt)
        registerAddToHeaderBtnClickListener()
        headerLayout.add(headerItemLayout, addToHeadersButton)
        propertyLayout.add(headerLayout)
    }

    private fun registerHeaderGrid() {
        headerGrid.setItems(headers)
        headerGrid.pageSize = 4
        headerGrid.setWidthFull()
        headerGrid.setColumns("key", "value")
        headerGrid.style.set("maxHeight", "200px")
        propertyLayout.add(headerGrid)
    }

    private fun registerJsonMessageArea() {
        messageTxtArea.label = "Message JSON"
        messageTxtArea.style.set("minHeight", "500px")
        messageTxtArea.setSizeFull()
        this.add(messageTxtArea)
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
                    brokerTxtField.value,
                    registryTextField.value
                )
            ))
            Notification.show("Message was sent into the given topic!", 3000, Notification.Position.MIDDLE)
            cleanUp()
        }
        this.add(sendBtn)
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
        //TODO clean up field after message was sent
    }

}