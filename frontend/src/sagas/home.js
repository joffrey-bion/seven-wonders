import { apply, call, put, take } from 'redux-saga/effects';
import { push } from 'react-router-redux';

import { actions, types } from '../redux/players';
import type { SevenWondersSession } from '../api/sevenWondersApi';

function* sendUsername(session: SevenWondersSession) {
  while (true) {
    const { username } = yield take(types.REQUEST_CHOOSE_USERNAME);
    yield apply(session, session.chooseName, [username]);
  }
}

function* validateUsername(session: SevenWondersSession) {
  const usernameChannel = yield apply(session, session.watchNameChoice, []);
  while (true) {
    const user = yield take(usernameChannel);
    yield put(actions.setCurrentPlayer(user));
    yield apply(usernameChannel, usernameChannel.close);
    yield put(push('/games'));
  }
}

function* homeSaga(session: SevenWondersSession) {
  yield [call(sendUsername, session), call(validateUsername, session)];
}

export default homeSaga;
