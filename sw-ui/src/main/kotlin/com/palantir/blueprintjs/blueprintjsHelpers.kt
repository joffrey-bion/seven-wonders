package com.palantir.blueprintjs

import org.luxons.sevenwonders.ui.utils.createElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import react.RBuilder
import react.RHandler
import react.ReactElement
import react.dom.*

typealias IconName = String

fun RBuilder.bpIcon(
    name: IconName,
    size: Int = Icon.SIZE_STANDARD,
    intent: Intent = Intent.NONE,
    title: String? = null,
    alt: String? = null,
    className: String? = null,
    block: RHandler<IIconProps> = {}
): ReactElement = child(Icon::class) {
    attrs {
        this.icon = name
        this.iconSize = size
        this.htmlTitle = title
        this.intent = intent
        this.title = alt
        this.className = className
    }
    block()
}

fun RBuilder.bpButton(
    minimal: Boolean = false,
    small: Boolean = false,
    large: Boolean = false,
    disabled: Boolean = false,
    title: String? = null,
    icon: IconName? = null,
    rightIcon: IconName? = null,
    intent: Intent = Intent.NONE,
    onClick: ((event: MouseEvent) -> Unit)? = {},
    block: RHandler<IButtonProps> = {}
): ReactElement = child(Button::class) {
    attrs {
        this.title = title
        this.minimal = minimal
        this.small = small
        this.large = large
        this.disabled = disabled
        this.icon = icon
        this.rightIcon = rightIcon
        this.intent = intent
        this.onClick = onClick
    }
    block()
}

fun RBuilder.bpButtonGroup(
    large: Boolean = false,
    minimal: Boolean = false,
    block: RHandler<IButtonGroupProps> = {}
): ReactElement = child(ButtonGroup::class) {
    attrs {
        this.large = large
        this.minimal = minimal
    }
    block()
}

fun RBuilder.bpInputGroup(
    large: Boolean = false,
    placeholder: String = "",
    rightElement: ReactElement? = null,
    onChange: (Event) -> Unit
): ReactElement = child(InputGroup::class) {
    attrs {
        this.large = large
        this.placeholder = placeholder
        this.rightElement = rightElement
        this.onChange = onChange
    }
}

fun RBuilder.bpTag(
    intent: Intent? = null,
    minimal: Boolean? = null,
    active: Boolean? = null,
    block: RHandler<ITagProps> = {}
): ReactElement = child(Tag::class) {
    attrs {
        this.intent = intent
        this.minimal = minimal
        this.active = active
    }
    block()
}

fun RBuilder.bpNonIdealState(
    icon: IconName? = null,
    title: ReactElement? = null,
    description: ReactElement? = null,
    action: ReactElement? = null,
    children: ReactElement? = null,
    block: RHandler<INonIdealStateProps> = {}
): ReactElement = child(NonIdealState::class) {
    attrs {
        this.icon = icon
        this.title = title
        this.description = description
        this.action = action
        this.children = children
    }
    block()
}

fun RBuilder.bpNonIdealState(
    icon: IconName? = null,
    title: String,
    description: ReactElement? = null,
    action: ReactElement? = null,
    children: ReactElement? = null,
    block: RHandler<INonIdealStateProps> = {}
): ReactElement = bpNonIdealState(icon, createElement { h2 { +title } }, description, action, children, block)

fun RBuilder.bpOverlay(
    isOpen: Boolean,
    autoFocus: Boolean = true,
    enforceFocus: Boolean = true,
    usePortal: Boolean = true,
    hasBackdrop: Boolean = true,
    canEscapeKeyClose: Boolean = true,
    canOutsideClickClose: Boolean = true,
    onClose: () -> Unit = {},
    block: RHandler<IOverlayProps> = {}
): ReactElement = child(Overlay::class) {
    attrs {
        this.isOpen = isOpen
        this.autoFocus = autoFocus
        this.enforceFocus = enforceFocus
        this.usePortal = usePortal
        this.hasBackdrop = hasBackdrop
        this.canEscapeKeyClose = canEscapeKeyClose
        this.canOutsideClickClose = canOutsideClickClose
        this.onClose = { onClose() }
    }
    block()
}

fun RBuilder.bpCallout(
    intent: Intent? = Intent.NONE,
    icon: IconName? = null,
    title: String? = null,
    block: RHandler<ICalloutProps> = {}
): ReactElement = child(Callout::class) {
    attrs {
        if (icon != null) {
            this.icon = icon
        }
        this.title = title
        this.intent = intent
    }
    block()
}
