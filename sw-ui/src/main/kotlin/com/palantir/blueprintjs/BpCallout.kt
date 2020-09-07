@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import react.PureComponent
import react.RState

external interface ICalloutProps : IIntentProps, IProps {
    var icon: dynamic /* IconName | MaybeElement */
        get() = definedExternally
        set(value) = definedExternally
    override var intent: Intent?
        get() = definedExternally
        set(value) = definedExternally
    var title: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Callout : PureComponent<ICalloutProps, RState> {
    override fun render(): react.ReactElement
    open var getIconName: Any

    companion object {
        var displayName: String
    }
}
