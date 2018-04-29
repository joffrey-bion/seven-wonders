// @flow
import { normalize } from 'normalizr';
import { push } from 'react-router-redux';
import type { Channel } from 'redux-saga';
import { eventChannel } from 'redux-saga';
import { all, apply, call, put, take } from 'redux-saga/effects';
import { SevenWondersSession } from '../api/sevenWondersApi';
import { actions as gameActions, types } from '../redux/games';
import { actions as playerActions } from '../redux/players';
import { game as gameSchema } from '../schemas/games';

function getCurrentGameId(): number {
  const path = window.location.pathname;
  return path.split('lobby/')[1];
}

function* watchLobbyUpdates(session: SevenWondersSession): * {
  const currentGameId: number = getCurrentGameId();
  const lobbyUpdatesChannel: Channel = yield eventChannel(session.watchLobbyUpdated(currentGameId));
  try {
    while (true) {
      const lobby = yield take(lobbyUpdatesChannel);
      const normalized = normalize(lobby, gameSchema);
      yield put(gameActions.updateGames(normalized.entities.games));
      yield put(playerActions.updatePlayers(normalized.entities.players));
    }
  } finally {
    yield apply(lobbyUpdatesChannel, lobbyUpdatesChannel.close);
  }
}

function* watchGameStart(session: SevenWondersSession): * {
  const currentGameId = getCurrentGameId();
  const gameStartedChannel = yield eventChannel(session.watchGameStarted(currentGameId));
  try {
    yield take(gameStartedChannel);
    yield put(gameActions.enterGame());
    yield put(push('/game'));
  } finally {
    yield apply(gameStartedChannel, gameStartedChannel.close);
  }
}

function* startGame(session: SevenWondersSession): * {
  while (true) {
    yield take(types.REQUEST_START_GAME);
    yield apply(session, session.startGame, []);
  }
}

export function* lobbySaga(session: SevenWondersSession): * {
  yield all([call(watchLobbyUpdates, session), call(watchGameStart, session), call(startGame, session)]);
}
