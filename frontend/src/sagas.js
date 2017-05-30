// @flow
import { router } from 'redux-saga-router';
import { call, fork } from 'redux-saga/effects';

import { makeSagaRoutes } from './routes';
import { createWsConnection } from './utils/websocket';
import errorHandlingSaga from './sagas/errors';

import type { SocketObjectType } from './utils/websocket';
import type { History } from 'react-router';

export default function* rootSaga(history: History): * {
  let wsConnection: SocketObjectType | void;
  try {
    wsConnection = yield call(createWsConnection);
  } catch (error) {
    console.error('Could not connect to socket');
    return;
  }
  yield fork(errorHandlingSaga, wsConnection);
  yield* router(history, makeSagaRoutes(wsConnection));
}
