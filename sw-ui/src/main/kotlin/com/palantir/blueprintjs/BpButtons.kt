@file:JsModule("@blueprintjs/core")
package com.palantir.blueprintjs

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import react.PureComponent
import react.RState
import react.ReactElement

/**
 * Interface for a clickable action, such as a button or menu item.
 * These props can be spready directly to a `<Button>` or `<MenuItem>` element.
 */
external interface IActionProps : IIntentProps, IProps {
    /** Whether this action is non-interactive. */
    var disabled: Boolean?
    /** Name of a Blueprint UI icon (or an icon element) to render before the text. */
    var icon: IconName?
    /** Click event handler. */
    var onClick: ((event: MouseEvent) -> Unit)?
    /** Action text. Can be any single React renderable. */
    var text: String?
}

external interface IButtonProps : IActionProps {
    // artificially added to allow title on button (should probably be on more general props)
    var title: String?
    /**
     * If set to `true`, the button will display in an active state.
     * This is equivalent to setting `className={Classes.ACTIVE}`.
     * @default false
     */
    var active: Boolean?
    /**
     * Text alignment within button. By default, icons and text will be centered
     * within the button. Passing `"left"` or `"right"` will align the button
     * text to that side and push `icon` and `rightIcon` to either edge. Passing
     * `"center"` will center the text and icons together.
     * @default Alignment.CENTER
     */
    var alignText: Alignment?
    /** A ref handler that receives the native HTML element backing this component. */
    var elementRef: ((ref: HTMLElement?) -> Any)?
    /** Whether this button should expand to fill its container. */
    var fill: Boolean?
    /** Whether this button should use large styles. */
    var large: Boolean?
    /**
     * If set to `true`, the button will display a centered loading spinner instead of its contents.
     * The width of the button is not affected by the value of this prop.
     * @default false
     */
    var loading: Boolean?
    /** Whether this button should use minimal styles. */
    var minimal: Boolean?
    /** Whether this button should use outlined styles. */
    var outlined: Boolean?
    /** Name of a Blueprint UI icon (or an icon element) to render after the text. */
    var rightIcon: IconName?
    /** Whether this button should use small styles. */
    var small: Boolean?
    /**
     * HTML `type` attribute of button. Accepted values are `"button"`, `"submit"`, and `"reset"`.
     * Note that this prop has no effect on `AnchorButton`; it only affects `Button`.
     * @default "button"
     */
    var type: String? // "submit" | "reset" | "button";
}

external interface IButtonState : RState {
    var isActive: Boolean
}

abstract external class AbstractButton : PureComponent<IButtonProps, IButtonState>

external class Button : AbstractButton {
    override fun render(): ReactElement
}
external class AnchorButton : AbstractButton {
    override fun render(): ReactElement
}

external interface IButtonGroupProps : IProps {
    /**
     * Text alignment within button. By default, icons and text will be centered
     * within the button. Passing `"left"` or `"right"` will align the button
     * text to that side and push `icon` and `rightIcon` to either edge. Passing
     * `"center"` will center the text and icons together.
     */
    var alignText: Alignment?

    /**
     * Whether the button group should take up the full width of its container.
     * @default false
     */
    var fill: Boolean?

    /**
     * Whether the child buttons should appear with minimal styling.
     * @default false
     */
    var minimal: Boolean?

    /**
     * Whether the child buttons should appear with large styling.
     * @default false
     */
    var large: Boolean?

    /**
     * Whether the button group should appear with vertical styling.
     * @default false
     */
    var vertical: Boolean?
}

external class ButtonGroup : PureComponent<IButtonGroupProps, RState> {
    override fun render(): ReactElement?
}
