package org.luxons.sevenwonders.ui.redux

import react.*
import react.redux.rConnect
import redux.RAction
import redux.WrapperAction
import kotlin.reflect.KClass

fun <DP : PropsWithChildren> connectDispatch(
    clazz: KClass<out RComponent<DP, out State>>,
    mapDispatchToProps: DP.((RAction) -> WrapperAction, PropsWithChildren) -> Unit,
): ComponentClass<PropsWithChildren> {
    val connect = rConnect(mapDispatchToProps = mapDispatchToProps)
    return connect.invoke(clazz.js.unsafeCast<ComponentClass<DP>>())
}

fun <SP : PropsWithChildren> connectState(
    clazz: KClass<out RComponent<SP, out State>>,
    mapStateToProps: SP.(SwState, PropsWithChildren) -> Unit,
): ComponentClass<PropsWithChildren> {
    val connect = rConnect(mapStateToProps = mapStateToProps)
    return connect.invoke(clazz.js.unsafeCast<ComponentClass<SP>>())
}

fun <SP : PropsWithChildren, OP : PropsWithChildren> connectStateWithOwnProps(
    clazz: KClass<out RComponent<SP, out State>>,
    mapStateToProps: SP.(SwState, OP) -> Unit,
): ComponentClass<OP> {
    val connect = rConnect(mapStateToProps = mapStateToProps)
    return connect.invoke(clazz.js.unsafeCast<ComponentClass<SP>>())
}

fun <SP : PropsWithChildren, DP : PropsWithChildren, P : PropsWithChildren> connectStateAndDispatch(
    clazz: KClass<out RComponent<P, out State>>,
    mapStateToProps: SP.(SwState, PropsWithChildren) -> Unit,
    mapDispatchToProps: DP.((RAction) -> WrapperAction, PropsWithChildren) -> Unit,
): ComponentClass<PropsWithChildren> {
    val connect = rConnect<SwState, RAction, WrapperAction, PropsWithChildren, SP, DP, P>(
        mapStateToProps = mapStateToProps,
        mapDispatchToProps = mapDispatchToProps,
    )
    return connect.invoke(clazz.js.unsafeCast<ComponentClass<P>>())
}
