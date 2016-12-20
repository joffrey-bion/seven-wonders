import { put, take } from 'redux-saga/effects'
import { eventChannel } from 'redux-saga'

import { NEW_GAME, JOIN_GAME } from './constants'
import { newGame, joinGame } from './actions'

function createSocketChannel(socket) {
  return eventChannel(emit => {
    const makeHandler = (type) => (event) => {
      const response = JSON.parse(event.body)
      emit({
        type,
        response
      })
    }

    const newGameHandler = makeHandler(NEW_GAME)
    const joinGameHandler = makeHandler(JOIN_GAME)

    const newGame = socket.subscribe('/topic/games', newGameHandler)
    const joinGame = socket.subscribe('/user/queue/join-game', joinGameHandler)

    const unsubscribe = () => {
      newGame.unsubscribe()
      joinGame.unsubscribe()
    }

    return unsubscribe
  })
}

export function* watchOnNewGames(socketConnection) {

  const { socket } = socketConnection
  const socketChannel = createSocketChannel(socket)

  while (true) {
    const { type, response } = yield take(socketChannel)

    switch (type) {
      case NEW_GAME:
        yield put(newGame(response))
        break;
      case JOIN_GAME:
        yield put(joinGame(response))
        break;
      default:
        console.error('Unknown type')
    }
  }
}

export default watchOnNewGames
