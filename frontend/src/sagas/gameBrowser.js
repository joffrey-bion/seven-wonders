import { call, put, take } from 'redux-saga/effects'
import { eventChannel } from 'redux-saga'
import { fromJS } from 'immutable'
import { push } from 'react-router-redux'

import { normalize } from 'normalizr'
import gameSchema from '../schemas/games'

import { actions as gameActions, types } from '../redux/games'
import { actions as playerActions } from '../redux/players'

function gameBrowserChannel(socket) {
  return eventChannel(emit => {

    const makeHandler = type => event => {
      const response = fromJS(JSON.parse(event.body))
      emit({ type, response })
    }

    const newGame = socket.subscribe('/topic/games', makeHandler(types.CREATE_OR_UPDATE_GAMES))
    const joinGame = socket.subscribe('/user/queue/lobby/joined', makeHandler(types.JOIN_GAME))

    return () => {
      newGame.unsubscribe()
      joinGame.unsubscribe()
    }
  })
}

export function *watchGames({ socket }) {
  const socketChannel = gameBrowserChannel(socket)

  try {
    while (true) {
      const { type, response } = yield take(socketChannel)

      switch (type) {
        case types.CREATE_OR_UPDATE_GAMES:
          const normalizedResponse = normalize(response.toJS(), gameSchema)
          yield put(playerActions.setPlayers(fromJS(normalizedResponse.entities.players)))
          yield put(gameActions.createOrUpdateGame(fromJS(normalizedResponse.entities.games)))
          break
        case types.JOIN_GAME:
          yield put(gameActions.joinGame(response))
          socketChannel.close()
          yield put(push(`/lobby/${response.id}`))
          break
        default:
          console.error('Unknown type')
      }
    }
  } finally {
    console.info('gameBrowserChannel closed')
  }
}

export function *createGame({ socket }) {
  const { name } = yield take(types.CREATE_GAME)

  socket.send("/app/lobby/create", JSON.stringify({ gameName: name }), {})
}

export function *gameBrowserSaga(socketConnection) {
  yield [
    call(watchGames, socketConnection),
    call(createGame, socketConnection)
  ]
}

export default gameBrowserSaga
