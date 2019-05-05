import { eventChannel, SagaIterator } from 'redux-saga';
import { apply, call, put, take } from 'redux-saga/effects';
import { ApiPlayerTurnInfo, ApiPreparedCard, ApiTable } from '../api/model';
import { SevenWondersSession } from '../api/sevenWondersApi';
import { actions, REQUEST_PREPARE_MOVE, REQUEST_SAY_READY } from '../redux/actions/game';
import { ENTER_GAME } from '../redux/actions/lobby';

function* watchPlayerReady(session: SevenWondersSession, gameId: number) {
  const channel = yield eventChannel(session.watchPlayerReady(gameId));
  try {
    while (true) {
      const username = yield take(channel);
      yield put(actions.receivePlayerReady(username));
    }
  } finally {
    yield apply(channel, channel.close);
  }
}

function* watchTableUpdates(session: SevenWondersSession, gameId: number) {
  const channel = yield eventChannel(session.watchTableUpdates(gameId));
  try {
    while (true) {
      const table: ApiTable = yield take(channel);
      yield put(actions.receiveTableUpdate(table));
    }
  } finally {
    yield apply(channel, channel.close);
  }
}

function* watchPreparedCards(session: SevenWondersSession, gameId: number) {
  const channel = yield eventChannel(session.watchPreparedCards(gameId));
  try {
    while (true) {
      const preparedCard: ApiPreparedCard = yield take(channel);
      yield put(actions.receivePreparedCard(preparedCard));
    }
  } finally {
    yield apply(channel, channel.close);
  }
}

function* sayReady(session: SevenWondersSession): SagaIterator {
  while (true) {
    yield take(REQUEST_SAY_READY);
    yield apply(session, session.sayReady);
  }
}

function* prepareMove(session: SevenWondersSession): SagaIterator {
  while (true) {
    let action = yield take(REQUEST_PREPARE_MOVE);
    yield apply(session, session.prepareMove, [action.move]);
  }
}

function* watchTurnInfo(session: SevenWondersSession) {
  const channel = yield eventChannel(session.watchTurnInfo());
  try {
    while (true) {
      const turnInfo: ApiPlayerTurnInfo = yield take(channel);
      yield put(actions.receiveTurnInfo(turnInfo));
    }
  } finally {
    yield apply(channel, channel.close);
  }
}

export function* gameSaga(session: SevenWondersSession): SagaIterator {
  const { gameId } = yield take(ENTER_GAME);
  console.log('Entered game!', gameId);
  yield [
    call(watchPlayerReady, session, gameId),
    call(watchTableUpdates, session, gameId),
    call(watchPreparedCards, session, gameId),
    call(sayReady, session),
    call(prepareMove, session),
    call(watchTurnInfo, session)
  ];
}
