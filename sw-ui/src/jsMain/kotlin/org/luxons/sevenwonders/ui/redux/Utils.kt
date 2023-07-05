package org.luxons.sevenwonders.ui.redux

import react.*
import react.redux.*
import redux.*
import kotlin.reflect.*

fun <R> useSwSelector(selector: (SwState) -> R) = useSelector(selector)
fun useSwDispatch() = useDispatch<RAction, WrapperAction>()

fun <SP : Props, DP : Props, P : Props> connectStateAndDispatch(
    clazz: KClass<out Component<P, out State>>,
    mapStateToProps: SP.(SwState, Props) -> Unit,
    mapDispatchToProps: DP.((RAction) -> WrapperAction, Props) -> Unit,
): ComponentClass<Props> = connectStateAndDispatch(
    component = clazz.react,
    mapStateToProps = mapStateToProps,
    mapDispatchToProps = mapDispatchToProps,
)

fun <SP : Props, DP : Props, P : Props> connectStateAndDispatch(
    component: ComponentClass<P>,
    mapStateToProps: SP.(SwState, Props) -> Unit,
    mapDispatchToProps: DP.((RAction) -> WrapperAction, Props) -> Unit,
): ComponentClass<Props> {
    val connect = rConnect<SwState, RAction, WrapperAction, Props, SP, DP, P>(
        mapStateToProps = mapStateToProps,
        mapDispatchToProps = mapDispatchToProps,
    )
    return connect.invoke(component)
}
