import { apply, call, cancelled, put, take } from 'redux-saga/effects';

import { createSubscriptionChannel } from '../utils/websocket';
import { actions } from '../redux/errors';

import { toastr } from 'react-redux-toastr';

export default function* errorHandlingSaga({ socket }) {
  const errorChannel = yield call(
    createSubscriptionChannel,
    socket,
    '/user/queue/errors'
  );
  try {
    while (true) {
      const error = yield take(errorChannel);
      yield* handleOneError(error);
    }
  } finally {
    if (yield cancelled()) {
      console.log('Error management saga cancelled');
      yield apply(errorChannel, errorChannel.close);
    }
  }
}

function* handleOneError(err) {
  console.error('Error received on web socket channel', err);
  const msg = buildMsg(err);
  yield apply(toastr, toastr.error, [msg, { icon: 'error' }]);
  yield put(actions.errorReceived(err));
}

function buildMsg(err) {
  if (err.details.length > 0) {
    return err.details.map(d => d.message).join('\n');
  } else {
    return err.message;
  }
}
