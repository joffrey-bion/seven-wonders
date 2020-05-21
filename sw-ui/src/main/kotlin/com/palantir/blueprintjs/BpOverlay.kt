@file:JsModule("@blueprintjs/core")
package com.palantir.blueprintjs

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.PureComponent
import react.RProps
import react.RState
import react.ReactElement

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
    var backdropProps: RProps? // React.HTMLProps<HTMLDivElement>?
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
