// @flow
import { push } from 'react-router-redux';
import { eventChannel } from 'redux-saga';
import { apply, call, put, take, all } from 'redux-saga/effects';
import type { ApiPlayer } from '../api/model';
import type { SevenWondersSession } from '../api/sevenWondersApi';

import { actions, types } from '../redux/players';

function* sendUsername(session: SevenWondersSession): * {
  while (true) {
    const { username } = yield take(types.REQUEST_CHOOSE_USERNAME);
    // $FlowFixMe
    yield apply(session, session.chooseName, [username]);
  }
}

function* validateUsername(session: SevenWondersSession): * {
  const usernameChannel = yield eventChannel(session.watchNameChoice());
  while (true) {
    const user: ApiPlayer = yield take(usernameChannel);
    yield put(actions.setCurrentPlayer(user));
    yield apply(usernameChannel, usernameChannel.close);
    yield put(push('/games'));
  }
}

export function* homeSaga(session: SevenWondersSession): * {
  yield all([call(sendUsername, session), call(validateUsername, session)]);
}
