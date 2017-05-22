import { apply, call, put, take } from 'redux-saga/effects';
import { createSubscriptionChannel } from '../utils/websocket';
import { push } from 'react-router-redux';

import { actions, types } from '../redux/players';

function* sendUsername({ socket }) {
  while (true) {
    const { username } = yield take(types.REQUEST_CHOOSE_USERNAME);
    yield apply(socket, socket.send, [
      '/app/chooseName',
      JSON.stringify({ playerName: username }),
    ]);
  }
}

function* validateUsername({ socket }) {
  const usernameChannel = yield call(
    createSubscriptionChannel,
    socket,
    '/user/queue/nameChoice'
  );
  while (true) {
    const user = yield take(usernameChannel);
    yield put(actions.setCurrentPlayer(user));
    yield apply(usernameChannel, usernameChannel.close);
    yield put(push('/games'));
  }
}

function* usernameChoiceSaga(wsConnection) {
  // TODO: Run sendUsername in loop when we have the ability to cancel saga on route change
  yield [
    call(sendUsername, wsConnection),
    call(validateUsername, wsConnection),
  ];
}

export default usernameChoiceSaga;
