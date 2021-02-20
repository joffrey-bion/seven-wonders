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
        val ACTIVE: String = definedExternally
        val ALIGN_LEFT: String = definedExternally
        val ALIGN_RIGHT: String = definedExternally
        val DARK: String = definedExternally
        val DISABLED: String = definedExternally
        val FILL: String = definedExternally
        val FIXED: String = definedExternally
        val FIXED_TOP: String = definedExternally
        val INLINE: String = definedExternally
        val INTERACTIVE: String = definedExternally
        val LARGE: String = definedExternally
        val LOADING: String = definedExternally
        val MINIMAL: String = definedExternally
        val OUTLINED: String = definedExternally
        val MULTILINE: String = definedExternally
        val ROUND: String = definedExternally
        val SMALL: String = definedExternally
        val VERTICAL: String = definedExternally
        val POSITION_TOP: String = definedExternally
        val POSITION_BOTTOM: String = definedExternally
        val POSITION_LEFT: String = definedExternally
        val POSITION_RIGHT: String = definedExternally
        val ELEVATION_0: String = definedExternally
        val ELEVATION_1: String = definedExternally
        val ELEVATION_2: String = definedExternally
        val ELEVATION_3: String = definedExternally
        val ELEVATION_4: String = definedExternally
        val INTENT_PRIMARY: String = definedExternally
        val INTENT_SUCCESS: String = definedExternally
        val INTENT_WARNING: String = definedExternally
        val INTENT_DANGER: String = definedExternally
        val FOCUS_DISABLED: String = definedExternally
        val UI_TEXT: String = definedExternally
        val RUNNING_TEXT: String = definedExternally
        val MONOSPACE_TEXT: String = definedExternally
        val TEXT_LARGE: String = definedExternally
        val TEXT_SMALL: String = definedExternally
        val TEXT_MUTED: String = definedExternally
        val TEXT_DISABLED: String = definedExternally
        val TEXT_OVERFLOW_ELLIPSIS: String = definedExternally
        val BLOCKQUOTE: String = definedExternally
        val CODE: String = definedExternally
        val CODE_BLOCK: String = definedExternally
        val HEADING: String = definedExternally
        val LIST: String = definedExternally
        val LIST_UNSTYLED: String = definedExternally
        val RTL: String = definedExternally
        val ALERT: String = definedExternally
        val ALERT_BODY: String = definedExternally
        val ALERT_CONTENTS: String = definedExternally
        val ALERT_FOOTER: String = definedExternally
        val BREADCRUMB: String = definedExternally
        val BREADCRUMB_CURRENT: String = definedExternally
        val BREADCRUMBS: String = definedExternally
        val BREADCRUMBS_COLLAPSED: String = definedExternally
        val BUTTON: String = definedExternally
        val BUTTON_GROUP: String = definedExternally
        val BUTTON_SPINNER: String = definedExternally
        val BUTTON_TEXT: String = definedExternally
        val CALLOUT: String = definedExternally
        val CALLOUT_ICON: String = definedExternally
        val CARD: String = definedExternally
        val COLLAPSE: String = definedExternally
        val COLLAPSE_BODY: String = definedExternally
        val COLLAPSIBLE_LIST: String = definedExternally
        val CONTEXT_MENU: String = definedExternally
        val CONTEXT_MENU_POPOVER_TARGET: String = definedExternally
        val CONTROL_GROUP: String = definedExternally
        val DIALOG: String = definedExternally
        val DIALOG_CONTAINER: String = definedExternally
        val DIALOG_BODY: String = definedExternally
        val DIALOG_CLOSE_BUTTON: String = definedExternally
        val DIALOG_FOOTER: String = definedExternally
        val DIALOG_FOOTER_ACTIONS: String = definedExternally
        val DIALOG_HEADER: String = definedExternally
        val DIALOG_STEP: String = definedExternally
        val DIALOG_STEP_CONTAINER: String = definedExternally
        val DIALOG_STEP_TITLE: String = definedExternally
        val DIALOG_STEP_ICON: String = definedExternally
        val DIVIDER: String = definedExternally
        val DRAWER: String = definedExternally
        val DRAWER_BODY: String = definedExternally
        val DRAWER_FOOTER: String = definedExternally
        val DRAWER_HEADER: String = definedExternally
        val EDITABLE_TEXT: String = definedExternally
        val EDITABLE_TEXT_CONTENT: String = definedExternally
        val EDITABLE_TEXT_EDITING: String = definedExternally
        val EDITABLE_TEXT_INPUT: String = definedExternally
        val EDITABLE_TEXT_PLACEHOLDER: String = definedExternally
        val FLEX_EXPANDER: String = definedExternally
        val HTML_SELECT: String = definedExternally
        /** @deprecated prefer `<HTMLSelect>` component */
        val SELECT: String = definedExternally
        val HTML_TABLE: String = definedExternally
        val HTML_TABLE_BORDERED: String = definedExternally
        val HTML_TABLE_CONDENSED: String = definedExternally
        val HTML_TABLE_STRIPED: String = definedExternally
        val INPUT: String = definedExternally
        val INPUT_GHOST: String = definedExternally
        val INPUT_GROUP: String = definedExternally
        val INPUT_LEFT_CONTAINER: String = definedExternally
        val INPUT_ACTION: String = definedExternally
        val CONTROL: String = definedExternally
        val CONTROL_INDICATOR: String = definedExternally
        val CONTROL_INDICATOR_CHILD: String = definedExternally
        val CHECKBOX: String = definedExternally
        val RADIO: String = definedExternally
        val SWITCH: String = definedExternally
        val SWITCH_INNER_TEXT: String = definedExternally
        val FILE_INPUT: String = definedExternally
        val FILE_INPUT_HAS_SELECTION: String = definedExternally
        val FILE_UPLOAD_INPUT: String = definedExternally
        val FILE_UPLOAD_INPUT_CUSTOM_TEXT: String = definedExternally
        val KEY: String = definedExternally
        val KEY_COMBO: String = definedExternally
        val MODIFIER_KEY: String = definedExternally
        val HOTKEY: String = definedExternally
        val HOTKEY_LABEL: String = definedExternally
        val HOTKEY_COLUMN: String = definedExternally
        val HOTKEY_DIALOG: String = definedExternally
        val LABEL: String = definedExternally
        val FORM_GROUP: String = definedExternally
        val FORM_CONTENT: String = definedExternally
        val FORM_HELPER_TEXT: String = definedExternally
        val MENU: String = definedExternally
        val MENU_ITEM: String = definedExternally
        val MENU_ITEM_LABEL: String = definedExternally
        val MENU_SUBMENU: String = definedExternally
        val MENU_DIVIDER: String = definedExternally
        val MENU_HEADER: String = definedExternally
        val MULTISTEP_DIALOG: String = definedExternally
        val MULTISTEP_DIALOG_PANELS: String = definedExternally
        val MULTISTEP_DIALOG_LEFT_PANEL: String = definedExternally
        val MULTISTEP_DIALOG_RIGHT_PANEL: String = definedExternally
        val MULTISTEP_DIALOG_FOOTER: String = definedExternally
        val NAVBAR: String = definedExternally
        val NAVBAR_GROUP: String = definedExternally
        val NAVBAR_HEADING: String = definedExternally
        val NAVBAR_DIVIDER: String = definedExternally
        val NON_IDEAL_STATE: String = definedExternally
        val NON_IDEAL_STATE_VISUAL: String = definedExternally
        val NUMERIC_INPUT: String = definedExternally
        val OVERFLOW_LIST: String = definedExternally
        val OVERFLOW_LIST_SPACER: String = definedExternally
        val OVERLAY: String = definedExternally
        val OVERLAY_BACKDROP: String = definedExternally
        val OVERLAY_CONTAINER: String = definedExternally
        val OVERLAY_CONTENT: String = definedExternally
        val OVERLAY_INLINE: String = definedExternally
        val OVERLAY_OPEN: String = definedExternally
        val OVERLAY_SCROLL_CONTAINER: String = definedExternally
        val PANEL_STACK: String = definedExternally
        val PANEL_STACK_HEADER: String = definedExternally
        val PANEL_STACK_HEADER_BACK: String = definedExternally
        val PANEL_STACK_VIEW: String = definedExternally
        val POPOVER: String = definedExternally
        val POPOVER_ARROW: String = definedExternally
        val POPOVER_BACKDROP: String = definedExternally
        val POPOVER_CAPTURING_DISMISS: String = definedExternally
        val POPOVER_CONTENT: String = definedExternally
        val POPOVER_CONTENT_SIZING: String = definedExternally
        val POPOVER_DISMISS: String = definedExternally
        val POPOVER_DISMISS_OVERRIDE: String = definedExternally
        val POPOVER_OPEN: String = definedExternally
        val POPOVER_TARGET: String = definedExternally
        val POPOVER_WRAPPER: String = definedExternally
        val TRANSITION_CONTAINER: String = definedExternally
        val PROGRESS_BAR: String = definedExternally
        val PROGRESS_METER: String = definedExternally
        val PROGRESS_NO_STRIPES: String = definedExternally
        val PROGRESS_NO_ANIMATION: String = definedExternally
        val PORTAL: String = definedExternally
        val SKELETON: String = definedExternally
        val SLIDER: String = definedExternally
        val SLIDER_AXIS: String = definedExternally
        val SLIDER_HANDLE: String = definedExternally
        val SLIDER_LABEL: String = definedExternally
        val SLIDER_TRACK: String = definedExternally
        val SLIDER_PROGRESS: String = definedExternally
        val START: String = definedExternally
        val END: String = definedExternally
        val SPINNER: String = definedExternally
        val SPINNER_ANIMATION: String = definedExternally
        val SPINNER_HEAD: String = definedExternally
        val SPINNER_NO_SPIN: String = definedExternally
        val SPINNER_TRACK: String = definedExternally
        val TAB: String = definedExternally
        val TAB_INDICATOR: String = definedExternally
        val TAB_INDICATOR_WRAPPER: String = definedExternally
        val TAB_LIST: String = definedExternally
        val TAB_PANEL: String = definedExternally
        val TABS: String = definedExternally
        val TAG: String = definedExternally
        val TAG_REMOVE: String = definedExternally
        val TAG_INPUT: String = definedExternally
        val TAG_INPUT_ICON: String = definedExternally
        val TAG_INPUT_VALUES: String = definedExternally
        val TOAST: String = definedExternally
        val TOAST_CONTAINER: String = definedExternally
        val TOAST_MESSAGE: String = definedExternally
        val TOOLTIP: String = definedExternally
        val TOOLTIP_INDICATOR: String = definedExternally
        val TREE: String = definedExternally
        val TREE_NODE: String = definedExternally
        val TREE_NODE_CARET: String = definedExternally
        val TREE_NODE_CARET_CLOSED: String = definedExternally
        val TREE_NODE_CARET_NONE: String = definedExternally
        val TREE_NODE_CARET_OPEN: String = definedExternally
        val TREE_NODE_CONTENT: String = definedExternally
        val TREE_NODE_EXPANDED: String = definedExternally
        val TREE_NODE_ICON: String = definedExternally
        val TREE_NODE_LABEL: String = definedExternally
        val TREE_NODE_LIST: String = definedExternally
        val TREE_NODE_SECONDARY_LABEL: String = definedExternally
        val TREE_NODE_SELECTED: String = definedExternally
        val TREE_ROOT: String = definedExternally
        val ICON: String = definedExternally
        /** @deprecated use <Icon> components and iconName prop APIs instead */
        @Deprecated("use <Icon> components and iconName prop APIs instead")
        val ICON_STANDARD: String = definedExternally
        /** @deprecated use <Icon> components and iconName prop APIs instead */
        @Deprecated("use <Icon> components and iconName prop APIs instead")
        val ICON_LARGE: String = definedExternally
    }
}
