import { router } from 'redux-saga-router'
import { call } from 'redux-saga/effects'

import createWsConnection from './utils/createWebSocketConnection'

import usernameChoiceSaga from './sagas/usernameChoice'

let wsConnection
const routes = {
  *'/'() {
    yield usernameChoiceSaga(wsConnection)
  }
}

export default function *rootSaga(history) {
  try {
    wsConnection = yield call(createWsConnection)
  } catch (error) {
    console.error('Could not connect to socket')
    return
  }

  yield* router(history, routes)
}
