package org.luxons.sevenwonders.ui.redux

import redux.RAction
import redux.Store
import redux.WrapperAction
import redux.createStore
import redux.rEnhancer

data class SwState(
    val playerName: String
)

val INITIAL_STATE = SwState("Bob")

fun configureStore(initialState: SwState = INITIAL_STATE): Store<SwState, RAction, WrapperAction> {

    // TODO configure middlewares

    return createStore(::rootReducer, initialState, rEnhancer())
}
