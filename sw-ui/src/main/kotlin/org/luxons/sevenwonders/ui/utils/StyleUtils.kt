package org.luxons.sevenwonders.ui.utils

import csstype.ClassName
import kotlinx.css.*
import styled.*
import kotlin.reflect.*

fun <T : StyleSheet> T.getTypedClassName(getClass: (T) -> KProperty0<RuleSet>): ClassName {
    return ClassName(getClassName(getClass))
}
