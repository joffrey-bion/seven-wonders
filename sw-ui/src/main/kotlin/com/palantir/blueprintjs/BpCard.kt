@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import org.w3c.dom.events.MouseEvent
import react.PureComponent
import react.RState
import react.ReactElement

external interface ICardProps : IProps {
    var elevation: Elevation?
        get() = definedExternally
        set(value) = definedExternally
    var interactive: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var onClick: ((e: MouseEvent) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Card : PureComponent<ICardProps, RState> {
    override fun render(): ReactElement

    companion object {
        var displayName: String
        var defaultProps: ICardProps
    }
}
