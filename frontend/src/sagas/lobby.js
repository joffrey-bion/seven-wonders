import { call, put, take, apply } from "redux-saga/effects";
import { createSubscriptionChannel } from "../utils/websocket";
import { push } from "react-router-redux";

import { normalize } from "normalizr";
import { game as gameSchema } from "../schemas/games";

import { actions as gameActions, types } from "../redux/games";
import { actions as playerActions } from "../redux/players";

function getCurrentGameId() {
  const path = window.location.pathname;
  return path.split("lobby/")[1];
}

function* watchLobbyUpdates({ socket }) {
  const currentGameId = getCurrentGameId();
  const lobbyUpdatesChannel = yield call(
    createSubscriptionChannel,
    socket,
    `/topic/lobby/${currentGameId}/updated`
  );
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

function* watchGameStart({ socket }) {
  const currentGameId = getCurrentGameId();
  const gameStartedChannel = yield call(
    createSubscriptionChannel,
    socket,
    `/topic/lobby/${currentGameId}/started`
  );
  try {
    yield take(gameStartedChannel);
    yield put(gameActions.enterGame());
    yield put(push("/game"));
  } finally {
    yield apply(gameStartedChannel, gameStartedChannel.close);
  }
}

function* startGame({ socket }) {
  while (true) {
    yield take(types.REQUEST_START_GAME);
    yield apply(socket, socket.send, ["/app/lobby/startGame", {}]);
  }
}

function* lobbySaga(socketConnection) {
  yield [
    call(watchLobbyUpdates, socketConnection),
    call(watchGameStart, socketConnection),
    call(startGame, socketConnection)
  ];
}

export default lobbySaga;
