// @flow
import { push } from 'react-router-redux';
import type { SagaIterator } from 'redux-saga';
import { eventChannel } from 'redux-saga';
import { all, apply, call, put, take } from 'redux-saga/effects';
import type { ApiPlayer } from '../api/model';
import type { SevenWondersSession } from '../api/sevenWondersApi';
import { actions } from '../redux/actions/players';
import { types } from '../redux/actions/players';

function* sendUsername(session: SevenWondersSession): SagaIterator {
  while (true) {
    const { username } = yield take(types.REQUEST_CHOOSE_USERNAME);
    // $FlowFixMe
    yield apply(session, session.chooseName, [username]);
  }
}

function* validateUsername(session: SevenWondersSession): SagaIterator {
  const usernameChannel = yield eventChannel(session.watchNameChoice());
  while (true) {
    const user: ApiPlayer = yield take(usernameChannel);
    yield put(actions.setCurrentPlayer(user));
    yield apply(usernameChannel, usernameChannel.close);
    yield put(push('/games'));
  }
}

export function* homeSaga(session: SevenWondersSession): SagaIterator {
  yield all([call(sendUsername, session), call(validateUsername, session)]);
}
