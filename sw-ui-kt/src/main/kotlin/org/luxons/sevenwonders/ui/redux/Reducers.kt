package org.luxons.sevenwonders.ui.redux

import redux.RAction

fun rootReducer(state: SwState, action: RAction) = when (action) {
    is RequestChooseName -> state.copy(playerName = action.playerName)
    else -> state
}
