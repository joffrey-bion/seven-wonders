@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import org.w3c.dom.HTMLInputElement
import react.PureComponent
import react.RState
import react.ReactElement

external interface IInputGroupProps : IControlledProps, IIntentProps, IProps {
    /**
     * Whether the input is non-interactive.
     * Note that `rightElement` must be disabled separately; this prop will not affect it.
     * @default false
     */
    var disabled: Boolean?

    /**
     * Whether the component should take up the full width of its container.
     */
    var fill: Boolean?

    /** Ref handler that receives HTML `<input>` element backing this component. */
    var inputRef: ((ref: HTMLInputElement?) -> Any)?

    /**
     * Name of a Blueprint UI icon (or an icon element) to render on the left side of the input group,
     * before the user's cursor.
     */
    var leftIcon: IconName?

    /** Whether this input should use large styles. */
    var large: Boolean?

    /** Whether this input should use small styles. */
    var small: Boolean?

    /** Placeholder text in the absence of any value. */
    var placeholder: String?

    /**
     * Element to render on right side of input.
     * For best results, use a minimal button, tag, or small spinner.
     */
    var rightElement: ReactElement?

    /** Whether the input (and any buttons) should appear with rounded caps. */
    var round: Boolean?

    /**
     * HTML `input` type attribute.
     * @default "text"
     */
    var type: String?
}

external interface IInputGroupState : RState {
    var rightElementWidth: Int
}

external class InputGroup : PureComponent<IInputGroupProps, IInputGroupState> {
    override fun render(): ReactElement
}
