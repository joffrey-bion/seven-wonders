// @flow
import { router } from 'redux-saga-router';
import { call, fork } from 'redux-saga/effects';

import { makeSagaRoutes } from './routes';
import errorHandlingSaga from './sagas/errors';

import type { History } from 'react-router';
import { SevenWondersSession, createSession } from './api/sevenWondersApi';

export default function* rootSaga(history: History): * {
  let sevenWondersSession: SevenWondersSession | void;
  try {
    sevenWondersSession = yield call(createSession);
  } catch (error) {
    console.error('Could not connect to socket', error);
    return;
  }
  yield fork(errorHandlingSaga, sevenWondersSession);
  yield* router(history, makeSagaRoutes(sevenWondersSession));
}
