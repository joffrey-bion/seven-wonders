@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import react.PureComponent
import react.RState
import react.ReactElement

// in BlueprintJS, IHTMLTableProps doesn't extend IProps, and yet className works fine...
external interface IHTMLTableProps : IProps {
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
