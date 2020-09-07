@file:JsModule("@blueprintjs/core")

package com.palantir.blueprintjs

import org.w3c.dom.events.Event
import react.RProps

/**
 * The four basic intents.
 */
// export declare const Intent: {
//    NONE: "none";
//    PRIMARY: "primary";
//    SUCCESS: "success";
//    WARNING: "warning";
//    DANGER: "danger";
// };
// export declare type Intent = typeof Intent[keyof typeof Intent];
external enum class Intent {
    NONE,
    PRIMARY,
    SUCCESS,
    WARNING,
    DANGER
}

/** Alignment along the horizontal axis. */
// export declare const Alignment: {
//    CENTER: "center";
//    LEFT: "left";
//    RIGHT: "right";
// };
// export declare type Alignment = typeof Alignment[keyof typeof Alignment];
external enum class Alignment {
    CENTER,
    LEFT,
    RIGHT
}

// export declare const Elevation: {
//    ZERO: 0;
//    ONE: 1;
//    TWO: 2;
//    THREE: 3;
//    FOUR: 4;
// };
// export declare type Elevation = typeof Elevation[keyof typeof Elevation];
external enum class Elevation {
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR
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

external class Classes {
    companion object {
        val HTML_TABLE: String = definedExternally
    }
}
