import { call, put, take } from 'redux-saga/effects'
import { eventChannel } from 'redux-saga'
import { fromJS } from 'immutable'
import { push } from 'react-router-redux'

import { actions, types } from '../../redux/game'

function createSocketChannel(socket) {
  return eventChannel(emit => {
    const makeHandler = (type) => (event) => {
      const response = fromJS(JSON.parse(event.body))

      emit({
        type,
        response
      })
    }

    const newGameHandler = makeHandler(types.NEW_GAME)
    const joinGameHandler = makeHandler(types.JOIN_GAME)

    const newGame = socket.subscribe('/topic/games', newGameHandler)
    const joinGame = socket.subscribe('/user/queue/join-game', joinGameHandler)

    const unsubscribe = () => {
      newGame.unsubscribe()
      joinGame.unsubscribe()
    }

    return unsubscribe
  })
}

export function* watchGames(socketConnection) {

  const { socket } = socketConnection
  const socketChannel = createSocketChannel(socket)

  while (true) {
    const { type, response } = yield take(socketChannel)

    switch (type) {
      case types.NEW_GAME:
        yield put(actions.newGame(response))
        break;
      case types.JOIN_GAME:
        yield put(actions.joinGame(response))
        break;
      default:
        console.error('Unknown type')
    }
  }
}

export function* createGame(socketConnection) {
  const { name } = yield take(types.CREATE_GAME)
  const { socket } = socketConnection

  socket.send("/app/lobby/create-game", JSON.stringify({
    'gameName': name,
    'playerName': 'Cesar92'
  }), {})
}

export function* gameBrowserSaga(socketConnection) {
  yield put(push('/lobby'))

  yield [
    call(watchGames, socketConnection),
    call(createGame, socketConnection)
  ]
}

export default gameBrowserSaga
