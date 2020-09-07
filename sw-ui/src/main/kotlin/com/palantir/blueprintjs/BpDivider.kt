@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import react.PureComponent
import react.RState
import react.ReactElement

external interface IDividerProps : IProps {
    var tagName: Any?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Divider : PureComponent<IDividerProps, RState> {
    override fun render(): ReactElement

    companion object {
        var displayName: String
    }
}
