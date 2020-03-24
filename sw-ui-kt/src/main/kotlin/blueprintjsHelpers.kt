package com.palantir.blueprintjs

import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import react.RBuilder
import react.RHandler
import react.ReactElement

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
    large: Boolean = false,
    disabled: Boolean = false,
    title: String? = null,
    icon: IconName? = null,
    rightIcon: IconName? = null,
    intent: Intent = Intent.NONE,
    onClick: ((event: MouseEvent) -> Unit)?,
    block: RHandler<IButtonProps> = {}
): ReactElement = child(Button::class) {
    attrs {
        this.title = title
        this.minimal = minimal
        this.large = large
        this.disabled = disabled
        this.icon = icon
        this.rightIcon = rightIcon
        this.intent = intent
        this.onClick = onClick
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
