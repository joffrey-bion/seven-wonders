@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.Event
import react.PureComponent
import react.RState
import react.ReactElement

external enum class PopoverInteractionKind {
    CLICK,
    CLICK_TARGET_ONLY,
    HOVER,
    HOVER_TARGET_ONLY
}

external enum class PopoverPosition {
    AUTO,
    AUTO_END,
    AUTO_START,
    BOTTOM,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
    LEFT,
    LEFT_BOTTOM,
    LEFT_TOP,
    RIGHT,
    RIGHT_BOTTOM,
    RIGHT_TOP,
    TOP,
    TOP_LEFT,
    TOP_RIGHT
}

external interface IPopoverSharedProps : IOverlayableProps, IProps {
    var boundary: Any?
        get() = definedExternally
        set(value) = definedExternally
    var captureDismiss: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var defaultIsOpen: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var disabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var hoverCloseDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var hoverOpenDelay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var inheritDarkTheme: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var isOpen: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var modifiers: Any?
        get() = definedExternally
        set(value) = definedExternally
    var onInteraction: ((nextOpenState: Boolean, e: Event) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var openOnTargetFocus: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var popoverClassName: String?
        get() = definedExternally
        set(value) = definedExternally
    var position: PopoverPosition?
        get() = definedExternally
        set(value) = definedExternally
    var targetClassName: String?
        get() = definedExternally
        set(value) = definedExternally
    var targetProps: Any?
        get() = definedExternally
        set(value) = definedExternally
    var targetTagName: Any?
        get() = definedExternally
        set(value) = definedExternally
    override var usePortal: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var wrapperTagName: Any?
        get() = definedExternally
        set(value) = definedExternally
}

external interface IPopoverProps : IPopoverSharedProps {
    var backdropProps: Any?
        get() = definedExternally
        set(value) = definedExternally
    var content: dynamic /* String? | JSX.Element? */
        get() = definedExternally
        set(value) = definedExternally
    var fill: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var interactionKind: PopoverInteractionKind?
        get() = definedExternally
        set(value) = definedExternally
    var hasBackdrop: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var minimal: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var popoverRef: ((ref: HTMLDivElement?) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var target: dynamic /* String? | JSX.Element? */
        get() = definedExternally
        set(value) = definedExternally
}

external interface IPopoverState : RState {
    var transformOrigin: String
    var isOpen: Boolean
    var hasDarkParent: Boolean
}

open external class Popover : PureComponent<IPopoverProps, IPopoverState> {
    override fun render(): ReactElement
}
