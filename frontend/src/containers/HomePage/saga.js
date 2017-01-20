import { call, put, take } from 'redux-saga/effects'
import { eventChannel } from 'redux-saga'
import { ENTER_GAME } from './actions'
import { setUsername } from '../UserRepo/actions'

import gameBrowserSaga from '../GameBrowser/saga'

function* sendUsername(socketConnection) {
  const { username: playerName } = yield take(ENTER_GAME)
  const { socket } = socketConnection

  socket.send("/app/chooseName", JSON.stringify({
    playerName
  }), {})
}

function createSocketChannel(socket) {
  return eventChannel(emit => {
    const receiveUsername = socket.subscribe('/user/queue/nameChoice', event => {
      emit(JSON.parse(event.body))
    })

    const unsubscribe = () => {
      receiveUsername.unsubscribe()
    }

    return unsubscribe
  })
}

function* validateUsername(socketConnection) {
  const { socket } = socketConnection
  const socketChannel = createSocketChannel(socket)

  const response = yield take(socketChannel)

  if (response.error) {
    return false
  }

  yield put(setUsername(response.userName, response.displayName, response.index))
  return true
}

function* homeSaga(socketConnection) {
  console.log('here')
  let validated = false
  do {
    const [, usernameValid] = yield [
      call(sendUsername, socketConnection),
      call(validateUsername, socketConnection)
    ]
    validated = usernameValid
  } while (!validated)
}

export default homeSaga
