// @flow
import type { History } from 'react-router';
import { router } from 'redux-saga-router';
import { call, fork } from 'redux-saga/effects';
import { connectToGame, SevenWondersSession } from './api/sevenWondersApi';
import { makeSagaRoutes } from './routes';
import errorHandlingSaga from './sagas/errors';

export default function* rootSaga(history: History): * {
  let sevenWondersSession: SevenWondersSession | void;
  try {
    sevenWondersSession = yield call(connectToGame);
  } catch (error) {
    console.error('Could not connect to socket', error);
    return;
  }
  yield fork(errorHandlingSaga, sevenWondersSession);
  yield* router(history, makeSagaRoutes(sevenWondersSession));
}
