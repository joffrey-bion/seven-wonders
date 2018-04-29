// @flow
import type { List, Map } from 'immutable';
import { fromJS } from 'immutable';
import type { Game, GameMapType, GameNormalMapType, GameShape } from '../models/games';
import { GamesState } from '../models/games';

export const types = {
  UPDATE_GAMES: 'GAMES/UPDATE_GAMES',
  REQUEST_CREATE_GAME: 'GAMES/REQUEST_CREATE_GAME',
  REQUEST_JOIN_GAME: 'GAMES/REQUEST_JOIN_GAME',
  REQUEST_START_GAME: 'GAMES/REQUEST_START_GAME',
  ENTER_LOBBY: 'GAMES/ENTER_LOBBY',
  ENTER_GAME: 'GAMES/ENTER_GAME',
};

export type UpdateGamesAction = { type: 'GAMES/UPDATE_GAMES', games: GameMapType };
export type RequestCreateGameAction = { type: 'GAMES/REQUEST_CREATE_GAME', gameName: string };
export type RequestJoinGameAction = { type: 'GAMES/REQUEST_JOIN_GAME', gameId: string };
export type RequestStartGameAction = { type: 'GAMES/REQUEST_START_GAME' };
export type EnterLobbyAction = { type: 'GAMES/ENTER_LOBBY', lobby: GameShape };
export type EnterGameAction = { type: 'GAMES/ENTER_GAME' };

export type GamesAction =
        | UpdateGamesAction
        | RequestCreateGameAction
        | RequestJoinGameAction
        | RequestStartGameAction
        | EnterLobbyAction
        | EnterGameAction;

export const actions = {
  updateGames: (games: GameNormalMapType): UpdateGamesAction => ({ type: types.UPDATE_GAMES, games: fromJS(games) }),
  requestJoinGame: (gameId: string): RequestJoinGameAction => ({ type: types.REQUEST_JOIN_GAME, gameId }),
  requestCreateGame: (gameName: string): RequestCreateGameAction => ({ type: types.REQUEST_CREATE_GAME, gameName }),
  requestStartGame: (): RequestStartGameAction => ({ type: types.REQUEST_START_GAME }),
  enterLobby: (lobby: GameShape): EnterLobbyAction => ({ type: types.ENTER_LOBBY, lobby: fromJS(lobby) }),
  enterGame: (): EnterGameAction => ({ type: types.ENTER_GAME }),
};

export const gamesReducer = (state: GamesState = new GamesState(), action: GamesAction) => {
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
