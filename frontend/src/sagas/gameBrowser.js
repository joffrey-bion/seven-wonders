import { call, put, take, apply } from 'redux-saga/effects'
import { createSubscriptionChannel } from '../utils/websocket'
import { push } from 'react-router-redux'

import { normalize } from 'normalizr'
import { game as gameSchema, gameList as gameListSchema } from '../schemas/games'

import { actions as gameActions, types } from '../redux/games'
import { actions as playerActions } from '../redux/players'

function *watchGames({socket}) {
  const gamesChannel = yield call(createSubscriptionChannel, socket, '/topic/games')
  try {
    while (true) {
      const gameList = yield take(gamesChannel)
      const normGameList = normalize(gameList, gameListSchema)
      // for an empty game array, there is no players/games entity maps
      yield put(playerActions.updatePlayers(normGameList.entities.players || {}))
      yield put(gameActions.updateGames(normGameList.entities.games || {}))
    }
  } finally {
    yield apply(gamesChannel, gamesChannel.close)
  }
}

function *watchLobbyJoined({socket}) {
  const joinedLobbyChannel = yield call(createSubscriptionChannel, socket, '/user/queue/lobby/joined')
  try {
    const joinedLobby = yield take(joinedLobbyChannel)
    const normalized = normalize(joinedLobby, gameSchema)
    const gameId = normalized.result
    yield put(playerActions.updatePlayers(normalized.entities.players))
    yield put(gameActions.updateGames(normalized.entities.games))
    yield put(gameActions.enterLobby(normalized.entities.games[gameId]))
    yield put(push(`/lobby/${gameId}`))
  } finally {
    yield apply(joinedLobbyChannel, joinedLobbyChannel.close)
  }
}

function *createGame({socket}) {
  while (true) {
    const {gameName} = yield take(types.REQUEST_CREATE_GAME)
    yield apply(socket, socket.send, ['/app/lobby/create', JSON.stringify({gameName}), {}])
  }
}

function *joinGame({socket}) {
  while (true) {
    const {gameId} = yield take(types.REQUEST_JOIN_GAME)
    yield apply(socket, socket.send, ['/app/lobby/join', JSON.stringify({gameId}), {}])
  }
}

function *gameBrowserSaga(socketConnection) {
  yield [
    call(watchGames, socketConnection),
    call(watchLobbyJoined, socketConnection),
    call(createGame, socketConnection),
    call(joinGame, socketConnection)
  ]
}

export default gameBrowserSaga
