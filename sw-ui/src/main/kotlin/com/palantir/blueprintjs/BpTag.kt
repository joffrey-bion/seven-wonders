@file:JsModule("@blueprintjs/core")
package com.palantir.blueprintjs

import org.w3c.dom.events.MouseEvent
import react.PureComponent
import react.RState
import react.ReactElement

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
    var onClick: ((e: MouseEvent) -> Unit)?
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
