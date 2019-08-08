package org.luxons.sevenwonders.ui.redux.sagas

import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.SwState
import redux.RAction
import redux.WrapperAction

fun gameSaga(session: SevenWondersSession, gameId: Long) = saga<SwState, RAction, WrapperAction> {
    fork(watchPlayerReady(session, gameId))
}

fun watchPlayerReady(session: SevenWondersSession, gameId: Long) = saga<SwState, RAction, WrapperAction> {
    session.watchPlayerReady(gameId)
}

