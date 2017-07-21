import {apply, cancelled, take} from 'redux-saga/effects';
import {toastr} from 'react-redux-toastr';
import {ApiError, SevenWondersSession} from '../api/sevenWondersApi';
import type {Channel} from 'redux-saga';

export default function* errorHandlingSaga(session: SevenWondersSession) {
  const errorChannel: Channel<ApiError> = yield apply(session, session.watchErrors, []);
  try {
    while (true) {
      const error: ApiError = yield take(errorChannel);
      yield* handleOneError(error);
    }
  } finally {
    if (yield cancelled()) {
      console.log('Error management saga cancelled');
      yield apply(errorChannel, errorChannel.close);
    }
  }
}

function* handleOneError(err: ApiError) {
  console.error('Error received on web socket channel', err);
  const msg = buildMsg(err);
  yield apply(toastr, toastr.error, [msg, { icon: 'error' }]);
}

function buildMsg(err: ApiError): string {
  if (err.details.length > 0) {
    return err.details.map(d => d.message).join('\n');
  } else {
    return err.message;
  }
}
