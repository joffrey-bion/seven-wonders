import { put, take } from 'redux-saga/effects'
import { eventChannel } from 'redux-saga'

function createSocketChannel(socket) {
  return eventChannel(emit => {
    const errorHandler = event => emit(JSON.parse(event))

    const userErrors = socket.subscribe('/user/queue/errors', errorHandler)

    const unsubscribe = () => {
      userErrors.unsubscribe()
    }

    return unsubscribe
  })
}

export function* watchOnErrors(socketConnection) {
  const { socket } = socketConnection
  const socketChannel = createSocketChannel(socket)

  while (true) {
    const payload = yield take(socketChannel)
    yield put({ type: 'USER_ERROR', payload })
  }
}

export default watchOnErrors
