import { router } from 'redux-saga-router'
import { call } from 'redux-saga/effects'

import createWsConnection from './utils/createWebSocketConnection'


// import errorSaga from './containers/App/saga'
import homeSaga from './containers/HomePage/saga'

let wsConnection
const routes = {
  *'/'() {
    yield console.info('home saga running')
    yield homeSaga(wsConnection)
  }
}

function* wsAwareSagas(history) {
  try {
    wsConnection = yield call(createWsConnection)
  } catch (error) {
    console.error('Could not connect to socket')
    return
  }

  yield* router(history, routes)
}

export default function* rootSaga(history) {
  yield [
    call(wsAwareSagas, history)
  ]
}
