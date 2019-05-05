import { push } from 'react-router-redux';
import { Channel, eventChannel, SagaIterator } from 'redux-saga';
import { all, apply, call, put, take } from 'redux-saga/effects';
import { SevenWondersSession } from '../api/sevenWondersApi';
import { actions as gameActions, ENTER_LOBBY, REQUEST_START_GAME } from '../redux/actions/lobby';

function* watchLobbyUpdates(session: SevenWondersSession, lobbyId: number): any {
  const lobbyUpdatesChannel: Channel<Object> = yield eventChannel(session.watchLobbyUpdated(lobbyId));
  try {
    while (true) {
      const lobby = yield take(lobbyUpdatesChannel);
      yield put(gameActions.updateGames([lobby]));
    }
  } finally {
    yield apply(lobbyUpdatesChannel, lobbyUpdatesChannel.close);
  }
}

function* watchGameStart(session: SevenWondersSession, lobbyId: number): any {
  const gameStartedChannel = yield eventChannel(session.watchGameStarted(lobbyId));
  try {
    yield take(gameStartedChannel);
    yield put(gameActions.enterGame(lobbyId));
    yield put(push(`/game/${lobbyId}`));
  } finally {
    yield apply(gameStartedChannel, gameStartedChannel.close);
  }
}

function* startGame(session: SevenWondersSession): SagaIterator {
  while (true) {
    yield take(REQUEST_START_GAME);
    yield apply(session, session.startGame);
  }
}

export function* lobbySaga(session: SevenWondersSession): SagaIterator {
  const { gameId } = yield take(ENTER_LOBBY);
  yield all([
    call(watchLobbyUpdates, session, gameId),
    call(watchGameStart, session, gameId),
    call(startGame, session)
  ]);
}
