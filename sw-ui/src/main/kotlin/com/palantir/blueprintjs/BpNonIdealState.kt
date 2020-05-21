@file:JsModule("@blueprintjs/core")
package com.palantir.blueprintjs

import react.PureComponent
import react.RState
import react.ReactElement

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
