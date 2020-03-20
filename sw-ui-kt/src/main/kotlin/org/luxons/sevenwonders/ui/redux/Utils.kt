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

inline fun <reified DP : RProps> connectDispatch(
    clazz: KClass<out RComponent<DP, out RState>>,
    noinline mapDispatchToProps: DP.((RAction) -> WrapperAction, RProps) -> Unit
): RClass<RProps> {
    val connect = rConnect(mapDispatchToProps = mapDispatchToProps)
    return connect.invoke(clazz.js.unsafeCast<RClass<DP>>())
}

inline fun <reified SP : RProps> connectState(
    clazz: KClass<out RComponent<SP, out RState>>,
    noinline mapStateToProps: SP.(SwState, RProps) -> Unit
): RClass<RProps> {
    val connect = rConnect(mapStateToProps = mapStateToProps)
    return connect.invoke(clazz.js.unsafeCast<RClass<SP>>())
}


inline fun <reified SP : RProps, reified DP : RProps, reified P : RProps> connect(
    clazz: KClass<out RComponent<P, out RState>>,
    noinline mapStateToProps: SP.(SwState, RProps) -> Unit,
    noinline mapDispatchToProps: DP.((RAction) -> WrapperAction, RProps) -> Unit
): RClass<RProps> {
    val connect = rConnect<SwState, RAction, WrapperAction, RProps, SP, DP, P>(
        mapStateToProps = mapStateToProps,
        mapDispatchToProps = mapDispatchToProps
    )
    return connect.invoke(clazz.js.unsafeCast<RClass<P>>())
}
