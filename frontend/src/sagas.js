import { router } from 'redux-saga-router'
import { call } from 'redux-saga/effects'

import { makeSagaRoutes } from './routes'
import createWsConnection from './utils/createWebSocketConnection'

export default function *rootSaga(history) {
  let wsConnection
  try {
    wsConnection = yield call(createWsConnection)
  } catch (error) {
    console.error('Could not connect to socket')
    return
  }

  yield* router(history, makeSagaRoutes(wsConnection))
}
