package com.palantir.blueprintjs

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import react.RBuilder
import react.RElementBuilder
import react.RHandler
import react.ReactElement

typealias IconName = String

fun RBuilder.icon(
    name: IconName,
    size: Int = Icon.SIZE_STANDARD,
    intent: Intent = Intent.NONE,
    title: String? = null,
    alt: String? = null
): ReactElement = child(Icon::class) {
    attrs {
        this.icon = name
        this.iconSize = size
        this.htmlTitle = title
        this.intent = intent
        this.title = alt
    }
}

fun RBuilder.bpButton(
    minimal: Boolean = false,
    large: Boolean = false,
    disabled: Boolean = false,
    icon: IconName? = null,
    rightIcon: IconName? = null,
    intent: Intent = Intent.NONE,
    onClick: ((event: MouseEvent) -> Unit)?,
    block: RHandler<IButtonProps> = {}
): ReactElement = child(Button::class) {
    attrs {
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

fun RBuilder.inputGroup(
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

data class ButtonProps(
    override var className: String? = null,
    override var intent: Intent? = Intent.NONE,
    override var disabled: Boolean? = false,
    override var icon: IconName? = null,
    override var onClick: ((event: MouseEvent) -> Unit)? = null,
    override var text: String? = null,
    override var active: Boolean? = true,
    override var alignText: Alignment? = null,
    override var elementRef: ((ref: HTMLElement?) -> Any)? = null,
    override var fill: Boolean? = null,
    override var large: Boolean? = null,
    override var loading: Boolean? = null,
    override var minimal: Boolean? = null,
    override var outlined: Boolean? = null,
    override var rightIcon: IconName? = null,
    override var small: Boolean? = null,
    override var type: String? = null
) : IButtonProps
