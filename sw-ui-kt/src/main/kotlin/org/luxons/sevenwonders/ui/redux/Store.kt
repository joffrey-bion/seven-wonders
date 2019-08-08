package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.ui.redux.sagas.SagaManager
import redux.RAction
import redux.Store
import redux.WrapperAction
import redux.applyMiddleware
import redux.compose
import redux.createStore
import redux.rEnhancer

data class SwState(
    val playerName: String
)

val INITIAL_STATE = SwState("Bob")

fun configureStore(
    sagaManager: SagaManager<SwState, RAction, WrapperAction>,
    initialState: SwState = INITIAL_STATE
): Store<SwState, RAction, WrapperAction> {
    val sagaEnhancer = applyMiddleware(sagaManager.createMiddleware())
    return createStore(::rootReducer, initialState, compose(sagaEnhancer, rEnhancer()))
}
