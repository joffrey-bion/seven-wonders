// @flow
import type { History } from 'react-router';
import { call, fork } from 'redux-saga/effects';
import { connectToGame, SevenWondersSession } from './api/sevenWondersApi';
import errorHandlingSaga from './sagas/errors';
import homeSaga from './sagas/home';
import gameBrowserSaga from './sagas/gameBrowser';
import lobbySaga from './sagas/lobby';

export default function* rootSaga(history: History): * {
  let sevenWondersSession: SevenWondersSession | void;
  try {
    sevenWondersSession = yield call(connectToGame);
  } catch (error) {
    console.error('Could not connect to socket', error);
    return;
  }
  yield fork(errorHandlingSaga, sevenWondersSession);
  yield fork(homeSaga, sevenWondersSession);
  yield fork(gameBrowserSaga, sevenWondersSession);
  yield fork(lobbySaga, sevenWondersSession);
}
