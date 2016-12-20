import { call, put, take } from 'redux-saga/effects'
import { eventChannel } from 'redux-saga'

import { NEW_GAME, JOIN_GAME, CREATE_GAME } from './constants'
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

export function* watchGames(socketConnection) {

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

export function* createGame(socketConnection) {
  const { name } = yield take(CREATE_GAME)
  const { socket } = socketConnection
  console.log(socket)
  socket.send("/app/lobby/create-game", JSON.stringify({
    'gameName': name,
    'playerName': 'Cesar92'
  }), {})
}

export function* gameBrowserSaga(socketConnection) {
  yield [
    call(watchGames, socketConnection),
    call(createGame, socketConnection)
  ]
}

export default gameBrowserSaga
