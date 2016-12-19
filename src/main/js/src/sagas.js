import { fork } from 'redux-saga/effects'

import counterSaga from './containers/Counter/saga'
import errorSaga from './containers/App/saga'

export default function* rootSaga() {
  yield fork(counterSaga)
  yield fork(errorSaga)
}
