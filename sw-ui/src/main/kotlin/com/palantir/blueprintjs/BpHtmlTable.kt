@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import react.PureComponent
import react.RProps
import react.RState
import react.ReactElement

external interface IHTMLTableProps : RProps {
    var bordered: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var condensed: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var interactive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var striped: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

open external class HTMLTable : PureComponent<IHTMLTableProps, RState> {
    override fun render(): ReactElement
}
