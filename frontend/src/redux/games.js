// @flow
import { fromJS } from 'immutable';
import GamesState from '../models/games';
import type { GameMapType, GameNormalMapType, GameShape } from "../models/games";
export const types = {
  UPDATE_GAMES: 'GAME/UPDATE_GAMES',
  REQUEST_CREATE_GAME: 'GAME/REQUEST_CREATE_GAME',
  REQUEST_JOIN_GAME: 'GAME/REQUEST_JOIN_GAME',
  REQUEST_START_GAME: 'GAME/REQUEST_JOIN_GAME',
  ENTER_LOBBY: 'GAME/ENTER_LOBBY',
  ENTER_GAME: 'GAME/ENTER_GAME',
};

type Actions = { type: 'GAME/UPDATE_GAMES', games: GameMapType } | { type: 'GAME/REQUEST_CREATE_GAME', gameId: string };

export const actions = {
  updateGames: (games: GameNormalMapType) => ({ type: types.UPDATE_GAMES, games: fromJS(games) }),
  requestJoinGame: (gameId: string) => ({ type: types.REQUEST_JOIN_GAME, gameId }),
  requestCreateGame: (gameName: string) => ({
    type: types.REQUEST_CREATE_GAME,
    gameName,
  }),
  requestStartGame: () => ({ type: types.REQUEST_START_GAME }),
  enterLobby: (lobby: GameShape) => ({ type: types.ENTER_LOBBY, lobby: fromJS(lobby) }),
  enterGame: () => ({ type: types.ENTER_GAME }),
};

export default (state: GamesState = new GamesState(), action: Actions) => {
  switch (action.type) {
    case types.UPDATE_GAMES:
      return state.addGames(action.games);
    case types.ENTER_LOBBY:
      return state.set('current', action.lobby.get('id'));
    default:
      return state;
  }
};

export const getAllGamesById = games => games.all;
export const getAllGames = games => getAllGamesById(games).toList();
export const getGame = (games, id) => getAllGamesById(games).get(`${id}`);
export const getCurrentGame = games => getGame(games, games.current);
