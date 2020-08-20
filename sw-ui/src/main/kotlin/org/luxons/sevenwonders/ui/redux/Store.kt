package org.luxons.sevenwonders.ui.redux

import kotlinx.browser.window
import org.luxons.sevenwonders.ui.redux.sagas.SagaManager
import redux.RAction
import redux.Store
import redux.WrapperAction
import redux.applyMiddleware
import redux.compose
import redux.createStore
import redux.rEnhancer

val INITIAL_STATE = SwState()

private fun <A, T1, R> composeWithDevTools(function1: (T1) -> R, function2: (A) -> T1): (A) -> R {
    val reduxDevtoolsExtensionCompose = window.asDynamic().__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    if (reduxDevtoolsExtensionCompose == undefined) {
        return compose(function1, function2)
    }
    return reduxDevtoolsExtensionCompose(function1, function2) as Function1<A, R>
}

fun configureStore(
    sagaManager: SagaManager<SwState, RAction, WrapperAction>,
    initialState: SwState = INITIAL_STATE
): Store<SwState, RAction, WrapperAction> {
    val sagaEnhancer = applyMiddleware(sagaManager.createMiddleware())
    return createStore(::rootReducer, initialState, composeWithDevTools(sagaEnhancer, rEnhancer()))
}
