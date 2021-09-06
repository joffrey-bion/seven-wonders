package org.luxons.sevenwonders.ui.redux

import react.*
import react.redux.rConnect
import redux.RAction
import redux.WrapperAction
import kotlin.reflect.KClass

inline fun <reified DP : PropsWithChildren> connectDispatch(
    clazz: KClass<out RComponent<DP, out State>>,
    noinline mapDispatchToProps: DP.((RAction) -> WrapperAction, PropsWithChildren) -> Unit,
): ComponentClass<PropsWithChildren> {
    val connect = rConnect(mapDispatchToProps = mapDispatchToProps)
    return connect.invoke(clazz.js.unsafeCast<ComponentClass<DP>>())
}

inline fun <reified SP : PropsWithChildren> connectState(
    clazz: KClass<out RComponent<SP, out State>>,
    noinline mapStateToProps: SP.(SwState, PropsWithChildren) -> Unit,
): ComponentClass<PropsWithChildren> {
    val connect = rConnect(mapStateToProps = mapStateToProps)
    return connect.invoke(clazz.js.unsafeCast<ComponentClass<SP>>())
}

inline fun <reified SP : PropsWithChildren, OP : PropsWithChildren> connectStateWithOwnProps(
    clazz: KClass<out RComponent<SP, out State>>,
    noinline mapStateToProps: SP.(SwState, OP) -> Unit,
): ComponentClass<OP> {
    val connect = rConnect(mapStateToProps = mapStateToProps)
    return connect.invoke(clazz.js.unsafeCast<ComponentClass<SP>>())
}

inline fun <reified SP : PropsWithChildren, reified DP : PropsWithChildren, reified P : PropsWithChildren> connectStateAndDispatch(
    clazz: KClass<out RComponent<P, out State>>,
    noinline mapStateToProps: SP.(SwState, PropsWithChildren) -> Unit,
    noinline mapDispatchToProps: DP.((RAction) -> WrapperAction, PropsWithChildren) -> Unit,
): ComponentClass<PropsWithChildren> {
    val connect = rConnect<SwState, RAction, WrapperAction, PropsWithChildren, SP, DP, P>(
        mapStateToProps = mapStateToProps,
        mapDispatchToProps = mapDispatchToProps,
    )
    return connect.invoke(clazz.js.unsafeCast<ComponentClass<P>>())
}
