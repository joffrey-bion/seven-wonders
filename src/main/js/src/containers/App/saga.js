import { call, put, take } from 'redux-saga/effects'
import { eventChannel } from 'redux-saga'
import createWebSocketConnection from "../../utils/createWebSocketConnection";

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

export function* watchOnErrors() {
  let socketChannel
  try {
    const { socket } = yield call(createWebSocketConnection)
    socketChannel = createSocketChannel(socket)
  } catch (error) {
    console.error('Error connecting to socket', error)
  }

  if (!socketChannel) {
    return
  }

  while (true) {
    const payload = yield take(socketChannel)
    yield put({ type: 'USER_ERROR', payload })
  }
}

export default watchOnErrors