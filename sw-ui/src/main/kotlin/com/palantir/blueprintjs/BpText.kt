@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import react.PureComponent
import react.RState
import react.ReactElement

external interface ITextProps : IProps {
    /**
     * Indicates that this component should be truncated with an ellipsis if it overflows its container.
     * The `title` attribute will also be added when content overflows to show the full text of the children on hover.
     * @default false
     */
    var ellipsize: Boolean?
    /**
     * HTML tag name to use for rendered element.
     * @default "div"
     */
    var tagName: String?
}

external interface ITextState : RState {
    var textContent: String
    var isContentOverflowing: Boolean
}

external class Text : PureComponent<ITextProps, ITextState> {
    override fun render(): ReactElement
}
