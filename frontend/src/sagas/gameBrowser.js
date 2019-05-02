// @flow
import { push } from 'react-router-redux';
import type { SagaIterator } from 'redux-saga';
import { eventChannel } from 'redux-saga';
import { all, apply, call, put, take } from 'redux-saga/effects';
import type { ApiLobby } from '../api/model';
import type { SevenWondersSession } from '../api/sevenWondersApi';
import { actions as gameActions, REQUEST_CREATE_GAME, REQUEST_JOIN_GAME } from '../redux/actions/lobby';

function* watchGames(session: SevenWondersSession): SagaIterator {
  const gamesChannel = yield eventChannel(session.watchGames());
  try {
    while (true) {
      const gameList = yield take(gamesChannel);
      yield put(gameActions.updateGames(gameList));
    }
  } finally {
    yield apply(gamesChannel, gamesChannel.close);
  }
}

function* watchLobbyJoined(session: SevenWondersSession): SagaIterator {
  const joinedLobbyChannel = yield eventChannel(session.watchLobbyJoined());
  try {
    const joinedLobby: ApiLobby = yield take(joinedLobbyChannel);
    yield put(gameActions.updateGames([joinedLobby]));
    yield put(gameActions.enterLobby(joinedLobby.id));
    yield put(push(`/lobby/${joinedLobby.id}`));
  } finally {
    yield apply(joinedLobbyChannel, joinedLobbyChannel.close);
  }
}

function* createGame(session: SevenWondersSession): SagaIterator {
  while (true) {
    const { gameName } = yield take(REQUEST_CREATE_GAME);
    // $FlowFixMe
    yield apply(session, session.createGame, [gameName]);
  }
}

function* joinGame(session: SevenWondersSession): SagaIterator {
  while (true) {
    const { gameId } = yield take(REQUEST_JOIN_GAME);
    // $FlowFixMe
    yield apply(session, session.joinGame, [gameId]);
  }
}

export function* gameBrowserSaga(session: SevenWondersSession): SagaIterator {
  yield all([
    call(watchGames, session),
    call(watchLobbyJoined, session),
    call(createGame, session),
    call(joinGame, session),
  ]);
}
