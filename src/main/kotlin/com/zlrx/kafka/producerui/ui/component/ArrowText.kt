package com.zlrx.kafka.producerui.ui.component

import com.vaadin.flow.component.dependency.StyleSheet
import com.vaadin.flow.component.html.Div

@StyleSheet("./pointer.css")
class ArrowText(val label: String, val msg: String) : Div() {

    init {
        this.className = ".pointer"
        this.add(Pointer(label))
        text = msg
    }

}

//@StyleSheet("pointer.css")
class Pointer(val label: String) : Div() {

    init {
        text = label
    }
}