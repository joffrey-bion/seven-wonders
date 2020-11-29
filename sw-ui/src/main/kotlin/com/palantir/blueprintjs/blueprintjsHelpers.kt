package com.palantir.blueprintjs

import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import react.RBuilder
import react.RHandler
import react.ReactElement
import react.buildElement
import react.dom.h2

typealias IconName = String

fun RBuilder.bpIcon(
    name: IconName,
    size: Int = Icon.SIZE_STANDARD,
    intent: Intent = Intent.NONE,
    title: String? = null,
    alt: String? = null,
    className: String? = null,
    block: RHandler<IIconProps> = {},
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
    block: RHandler<IButtonProps> = {},
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
    block: RHandler<IButtonGroupProps> = {},
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
    onChange: (Event) -> Unit,
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
    large: Boolean? = null,
    round: Boolean? = null,
    fill: Boolean? = null,
    active: Boolean? = null,
    icon: String? = null,
    block: RHandler<ITagProps> = {},
): ReactElement = child(Tag::class) {
    attrs {
        this.intent = intent
        this.minimal = minimal
        this.large = large
        this.round = round
        this.fill = fill
        this.icon = icon
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
    block: RHandler<INonIdealStateProps> = {},
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
    block: RHandler<INonIdealStateProps> = {},
): ReactElement = bpNonIdealState(icon, buildElement { h2 { +title } }, description, action, children, block)

fun RBuilder.bpOverlay(
    isOpen: Boolean,
    autoFocus: Boolean = true,
    enforceFocus: Boolean = true,
    usePortal: Boolean = true,
    hasBackdrop: Boolean = true,
    canEscapeKeyClose: Boolean = true,
    canOutsideClickClose: Boolean = true,
    onClose: () -> Unit = {},
    block: RHandler<IOverlayProps> = {},
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

fun RBuilder.bpDialog(
    isOpen: Boolean,
    title: ReactElement? = null,
    icon: ReactElement? = null,
    autoFocus: Boolean = true,
    enforceFocus: Boolean = true,
    usePortal: Boolean = true,
    hasBackdrop: Boolean = true,
    canEscapeKeyClose: Boolean = true,
    canOutsideClickClose: Boolean = true,
    isCloseButtonShown: Boolean = true,
    transitionName: String? = null,
    onClose: () -> Unit = {},
    block: RHandler<IDialogProps> = {},
): ReactElement = child(Dialog::class) {
    attrs {
        this.isOpen = isOpen
        if (title != null) {
            this.title = title
        }
        if (icon != null) {
            this.icon = icon
        }
        this.autoFocus = autoFocus
        this.enforceFocus = enforceFocus
        this.usePortal = usePortal
        this.hasBackdrop = hasBackdrop
        this.canEscapeKeyClose = canEscapeKeyClose
        this.canOutsideClickClose = canOutsideClickClose
        this.isCloseButtonShown = isCloseButtonShown
        if (transitionName != null) {
            this.transitionName = transitionName
        }
        this.onClose = { onClose() }
    }
    block()
}

fun RBuilder.bpDialog(
    isOpen: Boolean,
    title: String?,
    icon: IconName? = null,
    iconIntent: Intent = Intent.NONE,
    autoFocus: Boolean = true,
    enforceFocus: Boolean = true,
    usePortal: Boolean = true,
    hasBackdrop: Boolean = true,
    canEscapeKeyClose: Boolean = true,
    canOutsideClickClose: Boolean = true,
    isCloseButtonShown: Boolean = true,
    transitionName: String? = null,
    onClose: () -> Unit = {},
    block: RHandler<IDialogProps> = {},
): ReactElement = bpDialog(
    isOpen = isOpen,
    title = title?.let { buildElement { +title } },
    icon = icon?.let { buildElement { bpIcon(name = icon, intent = iconIntent) } },
    autoFocus = autoFocus,
    enforceFocus = enforceFocus,
    usePortal = usePortal,
    hasBackdrop = hasBackdrop,
    canEscapeKeyClose = canEscapeKeyClose,
    canOutsideClickClose = canOutsideClickClose,
    isCloseButtonShown = isCloseButtonShown,
    transitionName = transitionName,
    onClose = onClose,
    block = block
)

fun RBuilder.bpPopover(
    content: ReactElement,
    hoverOpenDelay: Number? = null,
    hoverCloseDelay: Number? = null,
    position: PopoverPosition = PopoverPosition.AUTO,
    interactionKind: PopoverInteractionKind = PopoverInteractionKind.HOVER,
    minimal: Boolean = false,
    canEscapeKeyClose: Boolean = true,
    className: String? = null,
    popoverClassName: String? = null,
    portalClassName: String? = null,
    onClose: () -> Unit = {},
    block: RHandler<IPopoverProps> = {},
): ReactElement = child(Popover::class) {
    attrs {
        this.interactionKind = interactionKind
        this.minimal = minimal
        this.content = content
        this.position = position
        this.hoverOpenDelay = hoverOpenDelay
        this.hoverCloseDelay = hoverCloseDelay
        this.canEscapeKeyClose = canEscapeKeyClose
        this.className = className
        this.popoverClassName = popoverClassName
        this.portalClassName = portalClassName
        this.onClose = { onClose() }
    }
    block()
}

fun RBuilder.bpCallout(
    intent: Intent? = Intent.NONE,
    icon: IconName? = null,
    title: String? = null,
    block: RHandler<ICalloutProps> = {},
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

fun RBuilder.bpCard(
    elevation: Elevation = Elevation.ZERO,
    interactive: Boolean = false,
    className: String? = null,
    onClick: () -> Unit = {},
    block: RHandler<ICardProps> = {},
): ReactElement = child(Card::class) {
    attrs {
        this.elevation = elevation
        this.interactive = interactive
        this.className = className
        this.onClick = { onClick() }
    }
    block()
}

fun RBuilder.bpHtmlTable(
    bordered: Boolean = false,
    interactive: Boolean = false,
    condensed: Boolean = false,
    striped: Boolean = false,
    block: RHandler<IHTMLTableProps> = {},
): ReactElement = child(HTMLTable::class) {
    attrs {
        this.bordered = bordered
        this.interactive = interactive
        this.condensed = condensed
        this.striped = striped
    }
    block()
}

fun RBuilder.bpDivider(
    tagName: String? = null,
    block: RHandler<IDividerProps> = {},
): ReactElement = child(Divider::class) {
    attrs {
        if (tagName != null) {
            this.tagName = tagName
        }
    }
    block()
}
