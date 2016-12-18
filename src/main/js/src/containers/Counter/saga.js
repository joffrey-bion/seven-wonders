import { put, call } from 'redux-saga/effects'
import { delay, takeEvery } from 'redux-saga'

import { increment } from './actions'
import { INCREMENT_ASYNC } from './constants'

export function* incrementAsync() {
    yield call(delay, 1000)
    yield put(increment())
}

export default function* counterSaga() {
    yield takeEvery(INCREMENT_ASYNC, incrementAsync)
}