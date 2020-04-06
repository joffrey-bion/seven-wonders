@file:JsModule("@blueprintjs/core")
package com.palantir.blueprintjs

import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import react.*

/**
 * The four basic intents.
 */
// export declare const Intent: {
//    NONE: "none";
//    PRIMARY: "primary";
//    SUCCESS: "success";
//    WARNING: "warning";
//    DANGER: "danger";
//};
//export declare type Intent = typeof Intent[keyof typeof Intent];
external enum class Intent {
    NONE,
    PRIMARY,
    SUCCESS,
    WARNING,
    DANGER
}

/** Alignment along the horizontal axis. */
//export declare const Alignment: {
//    CENTER: "center";
//    LEFT: "left";
//    RIGHT: "right";
//};
//export declare type Alignment = typeof Alignment[keyof typeof Alignment];
external enum class Alignment {
    CENTER,
    LEFT,
    RIGHT
}

/**
 * A shared base interface for all Blueprint component props.
 */
external interface IProps : RProps {
    /** A space-delimited list of class names to pass along to a child element. */
    var className: String?
}
external interface IIntentProps {
    /** Visual intent color to apply to element. */
    var intent: Intent?
}
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
/** Interface for a link, with support for customizing target window. */
external interface ILinkProps {
    /** Link URL. */
    var href: String?
    /** Link target attribute. Use `"_blank"` to open in a new window. */
    var target: String?
}
/** Interface for a controlled input. */
external interface IControlledProps {
    /** Initial value of the input, for uncontrolled usage. */
    var defaultValue: String?
    /** Change event handler. Use `event.target.value` for new value. */
    var onChange: ((Event) -> Unit)?
    /** Form value of the input, for controlled usage. */
    var value: String?
}
/**
 * An interface for an option in a list, such as in a `<select>` or `RadioGroup`.
 * These props can be spread directly to an `<option>` or `<Radio>` element.
 */
external interface IOptionProps : IProps {
    /** Whether this option is non-interactive. */
    var disabled: Boolean?
    /** Label text for this option. If omitted, `value` is used as the label. */
    var label: String?
    /** Value of this option. */
    var value: Any? // String | Number
}

external interface IIconProps : IIntentProps, IProps {
    /**
     * Color of icon. This is used as the `fill` attribute on the `<svg>` image
     * so it will override any CSS `color` property, including that set by
     * `intent`. If this prop is omitted, icon color is inherited from
     * surrounding text.
     */
    var color: String?
    /**
     * String for the `title` attribute on the rendered element, which will appear
     * on hover as a native browser tooltip.
     */
    var htmlTitle: String?
    /**
     * Name of a Blueprint UI icon, or an icon element, to render. This prop is
     * required because it determines the content of the component, but it can
     * be explicitly set to falsy values to render nothing.
     *
     * - If `null` or `undefined` or `false`, this component will render
     *   nothing.
     * - If given an `IconName` (a string literal union of all icon names), that
     *   icon will be rendered as an `<svg>` with `<path>` tags. Unknown strings
     *   will render a blank icon to occupy space.
     * - If given a `JSX.Element`, that element will be rendered and _all other
     *   props on this component are ignored._ This type is supported to
     *   simplify icon support in other Blueprint components. As a consumer, you
     *   should avoid using `<Icon icon={<Element />}` directly; simply render
     *   `<Element />` instead.
     */
    var icon: IconName
    /**
     * Size of the icon, in pixels. Blueprint contains 16px and 20px SVG icon
     * images, and chooses the appropriate resolution based on this prop.
     * @default Icon.SIZE_STANDARD = 16
     */
    var iconSize: Int?
    /** CSS style properties. */
    //var style: CSSProperties? // TODO
    /**
     * HTML tag to use for the rendered element.
     * @default "span"
     */
    var tagName: String? // keyof JSX.IntrinsicElements
    /**
     * Description string. This string does not appear in normal browsers, but
     * it increases accessibility. For instance, screen readers will use it for
     * aural feedback. By default, this is set to the icon's name. Pass an
     * explicit falsy value to disable.
     */
    var title: String?
}

external class Icon : PureComponent<IIconProps, RState> {

    override fun render(): ReactElement?

    companion object {
        val SIZE_STANDARD: Int = definedExternally
        val SIZE_LARGE: Int = definedExternally
    }
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
    var type: String? //"submit" | "reset" | "button";
}

external interface IButtonState : RState {
    var isActive: Boolean
}

abstract external class AbstractButton : PureComponent<IButtonProps, IButtonState> {
}

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
    var inputRef: ((ref: HTMLInputElement?) -> Any)?;
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

external interface ITagProps : IProps, IIntentProps {
    /**
     * Whether the tag should appear in an active state.
     * @default false
     */
    var active: Boolean?
    /**
     * Whether the tag should take up the full width of its container.
     * @default false
     */
    var fill: Boolean?
    /** Name of a Blueprint UI icon (or an icon element) to render before the children. */
    var icon: IconName?
    /**
     * Whether the tag should visually respond to user interactions. If set
     * to `true`, hovering over the tag will change its color and mouse cursor.
     *
     * Recommended when `onClick` is also defined.
     *
     * @default false
     */
    var interactive: Boolean?
    /**
     * Whether this tag should use large styles.
     * @default false
     */
    var large: Boolean?
    /**
     * Whether this tag should use minimal styles.
     * @default false
     */
    var minimal: Boolean?
    /**
     * Whether tag content should be allowed to occupy multiple lines.
     * If false, a single line of text will be truncated with an ellipsis if
     * it overflows. Note that icons will be vertically centered relative to
     * multiline text.
     * @default false
     */
    var multiline: Boolean?
    /**
     * Callback invoked when the tag is clicked.
     * Recommended when `interactive` is `true`.
     */
    var onClick: ((e: MouseEvent) -> Unit)?;
    /**
     * Click handler for remove button.
     * The remove button will only be rendered if this prop is defined.
     */
    var onRemove: ((e: MouseEvent, tagProps: ITagProps) -> Unit)?
    /** Name of a Blueprint UI icon (or an icon element) to render after the children. */
    var rightIcon: IconName?
    /**
     * Whether this tag should have rounded ends.
     * @default false
     */
    var round: Boolean?
}

external class Tag : PureComponent<ITagProps, RState> {
    override fun render(): ReactElement
}

external interface INonIdealStateProps : IProps {
    /** An action to resolve the non-ideal state which appears after `description`. */
    var action: ReactElement?

    /**
     * Advanced usage: React `children` will appear last (after `action`).
     * Avoid passing raw strings as they will not receive margins and disrupt the layout flow.
     */
    var children: ReactElement?

    /**
     * A longer description of the non-ideal state.
     * A string or number value will be wrapped in a `<div>` to preserve margins.
     */
    var description: ReactElement?

    /** The name of a Blueprint icon or a JSX Element (such as `<Spinner/>`) to render above the title. */
    var icon: IconName?

    /** The title of the non-ideal state. */
    var title: ReactElement?
}

external class NonIdealState : PureComponent<INonIdealStateProps, RState> {
    override fun render(): ReactElement?
}

external class Classes {
    companion object {
        val HTML_TABLE: String = definedExternally
    }
}

external interface IOverlayableProps : IOverlayLifecycleProps {
    /**
     * Whether the overlay should acquire application focus when it first opens.
     * @default true
     */
    var autoFocus: Boolean?
    /**
     * Whether pressing the `esc` key should invoke `onClose`.
     * @default true
     */
    var canEscapeKeyClose: Boolean?
    /**
     * Whether the overlay should prevent focus from leaving itself. That is, if the user attempts
     * to focus an element outside the overlay and this prop is enabled, then the overlay will
     * immediately bring focus back to itself. If you are nesting overlay components, either disable
     * this prop on the "outermost" overlays or mark the nested ones `usePortal={false}`.
     * @default true
     */
    var enforceFocus: Boolean?
    /**
     * If `true` and `usePortal={true}`, the `Portal` containing the children is created and attached
     * to the DOM when the overlay is opened for the first time; otherwise this happens when the
     * component mounts. Lazy mounting provides noticeable performance improvements if you have lots
     * of overlays at once, such as on each row of a table.
     * @default true
     */
    var lazy: Boolean?
    /**
     * Indicates how long (in milliseconds) the overlay's enter/leave transition takes.
     * This is used by React `CSSTransition` to know when a transition completes and must match
     * the duration of the animation in CSS. Only set this prop if you override Blueprint's default
     * transitions with new transitions of a different length.
     * @default 300
     */
    var transitionDuration: Int?
    /**
     * Whether the overlay should be wrapped in a `Portal`, which renders its contents in a new
     * element attached to `portalContainer` prop.
     *
     * This prop essentially determines which element is covered by the backdrop: if `false`,
     * then only its parent is covered; otherwise, the entire page is covered (because the parent
     * of the `Portal` is the `<body>` itself).
     *
     * Set this prop to `false` on nested overlays (such as `Dialog` or `Popover`) to ensure that they
     * are rendered above their parents.
     * @default true
     */
    var usePortal: Boolean?
    /**
     * Space-delimited string of class names applied to the `Portal` element if
     * `usePortal={true}`.
     */
    var portalClassName: String?
    /**
     * The container element into which the overlay renders its contents, when `usePortal` is `true`.
     * This prop is ignored if `usePortal` is `false`.
     * @default document.body
     */
    var portalContainer: HTMLElement?
    /**
     * A callback that is invoked when user interaction causes the overlay to close, such as
     * clicking on the overlay or pressing the `esc` key (if enabled).
     *
     * Receives the event from the user's interaction, if there was an event (generally either a
     * mouse or key event). Note that, since this component is controlled by the `isOpen` prop, it
     * will not actually close itself until that prop becomes `false`.
     */
    var onClose: ((Event) -> Unit)?
}
external interface IOverlayLifecycleProps {
    /**
     * Lifecycle method invoked just before the CSS _close_ transition begins on
     * a child. Receives the DOM element of the child being closed.
     */
    var onClosing: ((node: HTMLElement) -> Unit)?
    /**
     * Lifecycle method invoked just after the CSS _close_ transition ends but
     * before the child has been removed from the DOM. Receives the DOM element
     * of the child being closed.
     */
    var onClosed: ((node: HTMLElement) -> Unit)?
    /**
     * Lifecycle method invoked just after mounting the child in the DOM but
     * just before the CSS _open_ transition begins. Receives the DOM element of
     * the child being opened.
     */
    var onOpening: ((node: HTMLElement) -> Unit)?
    /**
     * Lifecycle method invoked just after the CSS _open_ transition ends.
     * Receives the DOM element of the child being opened.
     */
    var onOpened: ((node: HTMLElement) -> Unit)?
}
external interface IBackdropProps {
    /** CSS class names to apply to backdrop element. */
    var backdropClassName: String?
    /** HTML props for the backdrop element. */
    var backdropProps: RProps? //React.HTMLProps<HTMLDivElement>?
    /**
     * Whether clicking outside the overlay element (either on backdrop when present or on document)
     * should invoke `onClose`.
     * @default true
     */
    var canOutsideClickClose: Boolean?
    /**
     * Whether a container-spanning backdrop element should be rendered behind the contents.
     * @default true
     */
    var hasBackdrop: Boolean?
}
external interface IOverlayProps : IOverlayableProps, IBackdropProps, IProps {
    /**
     * Toggles the visibility of the overlay and its children.
     * This prop is required because the component is controlled.
     */
    var isOpen: Boolean
    /**
     * Name of the transition for internal `CSSTransition`.
     * Providing your own name here will require defining new CSS transition properties.
     * @default Classes.OVERLAY
     */
    var transitionName: String?
}
external interface IOverlayState : RState {
    var hasEverOpened: Boolean?
}
external class Overlay : PureComponent<IOverlayProps, IOverlayState> {
    override fun render(): ReactElement
}