package org.luxons.sevenwonders.ui.redux

import redux.RAction

fun rootReducer(state: SwState, action: RAction) = when (action) {
    is ChooseUserName -> state.copy(playerName = action.newUsername)
    else -> state
}
