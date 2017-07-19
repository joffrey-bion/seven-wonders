// @flow
import { fromJS } from 'immutable';
import GamesState from '../models/games';
import type { GameMapType, GameNormalMapType, GameShape, Game } from '../models/games';
import type { Map, List } from 'immutable';

export const types = {
  UPDATE_GAMES: 'GAMES/UPDATE_GAMES',
  REQUEST_CREATE_GAME: 'GAMES/REQUEST_CREATE_GAME',
  REQUEST_JOIN_GAME: 'GAMES/REQUEST_JOIN_GAME',
  REQUEST_START_GAME: 'GAMES/REQUEST_JOIN_GAME',
  ENTER_LOBBY: 'GAMES/ENTER_LOBBY',
  ENTER_GAME: 'GAMES/ENTER_GAME',
};

type Actions =
  | { type: "GAMES/UPDATE_GAMES", games: GameMapType }
  | { type: "GAMES/REQUEST_CREATE_GAME", gameId: string };

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

export const getAllGamesById = (games: GamesState): Map<string, Game> => games.all;
export const getAllGames = (games: GamesState): List<Game> => getAllGamesById(games).toList();
export const getGame = (games: GamesState, id: string | number): Game => getAllGamesById(games).get(`${id}`);
export const getCurrentGame = (games: GamesState) => getGame(games, games.current);
