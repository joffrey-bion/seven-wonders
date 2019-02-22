// @flow
import { normalize } from 'normalizr';
import { push } from 'react-router-redux';
import type { Channel, SagaIterator } from 'redux-saga';
import { eventChannel } from 'redux-saga';
import { all, apply, call, put, take } from 'redux-saga/effects';
import { SevenWondersSession } from '../api/sevenWondersApi';
import { actions as gameActions, types } from '../redux/actions/lobby';
import { actions as playerActions } from '../redux/actions/players';
import { game as gameSchema } from '../schemas/games';

function* watchLobbyUpdates(session: SevenWondersSession, lobbyId: number): SagaIterator {
  const lobbyUpdatesChannel: Channel = yield eventChannel(session.watchLobbyUpdated(lobbyId));
  try {
    while (true) {
      const lobby = yield take(lobbyUpdatesChannel);
      const normalized = normalize(lobby, gameSchema);
      // players update needs to be first, otherwise the UI cannot find the player in the list
      yield put(playerActions.updatePlayers(normalized.entities.players));
      yield put(gameActions.updateGames(normalized.entities.games));
    }
  } finally {
    yield apply(lobbyUpdatesChannel, lobbyUpdatesChannel.close);
  }
}

function* watchGameStart(session: SevenWondersSession, lobbyId: number): SagaIterator {
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
    yield take(types.REQUEST_START_GAME);
    yield apply(session, session.startGame, []);
  }
}

export function* lobbySaga(session: SevenWondersSession): SagaIterator {
  const { gameId } = yield take(types.ENTER_LOBBY);
  yield all([
    call(watchLobbyUpdates, session, gameId),
    call(watchGameStart, session, gameId),
    call(startGame, session)
  ]);
}
