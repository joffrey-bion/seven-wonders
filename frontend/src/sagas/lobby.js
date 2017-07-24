import { call, put, take, apply } from 'redux-saga/effects';
import type { Channel } from 'redux-saga';

import { push } from 'react-router-redux';

import { normalize } from 'normalizr';
import { game as gameSchema } from '../schemas/games';

import { actions as gameActions, types } from '../redux/games';
import { actions as playerActions } from '../redux/players';
import { SevenWondersSession } from '../api/sevenWondersApi';
import { createChannel } from './utils';

function getCurrentGameId(): number {
  const path = window.location.pathname;
  return path.split('lobby/')[1];
}

function* watchLobbyUpdates(session: SevenWondersSession) {
  const currentGameId: number = getCurrentGameId();
  const lobbyUpdatesChannel: Channel = yield createChannel(session, session.watchLobbyUpdated, currentGameId);
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

function* watchGameStart(session: SevenWondersSession) {
  const currentGameId = getCurrentGameId();
  const gameStartedChannel = yield createChannel(session, session.watchGameStarted, currentGameId);
  try {
    yield take(gameStartedChannel);
    yield put(gameActions.enterGame());
    yield put(push('/game'));
  } finally {
    yield apply(gameStartedChannel, gameStartedChannel.close);
  }
}

function* startGame(session: SevenWondersSession) {
  while (true) {
    yield take(types.REQUEST_START_GAME);
    yield apply(session, session.startGame, []);
  }
}

function* lobbySaga(session: SevenWondersSession) {
  yield [call(watchLobbyUpdates, session), call(watchGameStart, session), call(startGame, session)];
}

export default lobbySaga;
