package org.luxons.sevenwonders.ui.redux

import react.RClass
import react.RComponent
import react.RProps
import react.RState
import react.invoke
import react.redux.rConnect
import redux.RAction
import redux.WrapperAction
import kotlin.reflect.KClass

inline fun <reified T : RProps> connectDispatch(
    clazz: KClass<out RComponent<T, out RState>>,
    crossinline mapDispatchToProps: T.((RAction) -> WrapperAction, RProps) -> Unit
): RClass<RProps> {
    val connect = rConnect<RAction, WrapperAction, RProps, T>(mapDispatchToProps = { dispatch, ownProps ->
        mapDispatchToProps(dispatch, ownProps)
    })
    return connect.invoke(clazz.js.unsafeCast<RClass<T>>())
}
