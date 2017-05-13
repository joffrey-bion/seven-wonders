import { apply, call, cancelled, put, take } from 'redux-saga/effects'

import { createSubscriptionChannel } from '../utils/websocket'
import { actions } from '../redux/errors'

export default function *errorHandlingSaga({ socket }) {
  const errorChannel = yield call(createSubscriptionChannel, socket, '/user/queue/error')
  try {
    while (true) {
      const error = yield take(errorChannel)
      yield* handleOneError(error)
    }
  } finally {
    if (yield cancelled()) {
      yield apply(errorChannel, errorChannel.close)
    }
  }
}

function *handleOneError(error) {
  console.error("Error received on web socket channel", error)
  yield put(actions.errorReceived(error))
}
