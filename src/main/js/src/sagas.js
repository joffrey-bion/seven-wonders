import { fork } from 'redux-saga/effects'

import counterSaga from './containers/Counter/saga'

export default function* rootSaga() {
  yield fork(counterSaga)
}
