package com.zlrx.kafka.producerui.ui.component

import com.vaadin.flow.component.html.Span

class Divider : Span() {

    init {
        style.set("background-color", "lightblue")
        style.set("flex", "0 0 2px")
        style.set("align-self", "stretch")
    }

}