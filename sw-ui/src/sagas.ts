import { SagaIterator } from 'redux-saga';
import { call, fork } from 'redux-saga/effects';
import { connectToGame, SevenWondersSession } from './api/sevenWondersApi';
import { errorHandlingSaga } from './sagas/errors';
import { gameSaga } from './sagas/game';
import { gameBrowserSaga } from './sagas/gameBrowser';
import { homeSaga } from './sagas/home';
import { lobbySaga } from './sagas/lobby';

export function* rootSaga(): SagaIterator {
  let sevenWondersSession: SevenWondersSession;
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
  yield fork(gameSaga, sevenWondersSession);
}
