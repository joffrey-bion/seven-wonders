import { push } from 'react-router-redux';
import { eventChannel, SagaIterator } from 'redux-saga';
import { all, apply, call, put, take } from 'redux-saga/effects';
import { ApiPlayer } from '../api/model';
import { SevenWondersSession } from '../api/sevenWondersApi';
import { actions, REQUEST_CHOOSE_USERNAME } from '../redux/actions/user';

function* sendUsername(session: SevenWondersSession): SagaIterator {
  while (true) {
    const { username } = yield take(REQUEST_CHOOSE_USERNAME);
    // $FlowFixMe
    yield apply(session, session.chooseName, [username]);
  }
}

function* validateUsername(session: SevenWondersSession): any {
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
