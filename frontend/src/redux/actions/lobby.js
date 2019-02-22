import { fromJS } from 'immutable';
import type { GameMapType, GameNormalMapType } from '../../models/games';

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
export type RequestJoinGameAction = { type: 'GAMES/REQUEST_JOIN_GAME', gameId: number };
export type RequestStartGameAction = { type: 'GAMES/REQUEST_START_GAME' };
export type EnterLobbyAction = { type: 'GAMES/ENTER_LOBBY', gameId: number };
export type EnterGameAction = { type: 'GAMES/ENTER_GAME', gameId: number };

export type LobbyAction =
        | UpdateGamesAction
        | RequestCreateGameAction
        | RequestJoinGameAction
        | RequestStartGameAction
        | EnterLobbyAction
        | EnterGameAction;

export const actions = {
  updateGames: (games: GameNormalMapType): UpdateGamesAction => ({ type: types.UPDATE_GAMES, games: fromJS(games) }),
  requestJoinGame: (gameId: number): RequestJoinGameAction => ({ type: types.REQUEST_JOIN_GAME, gameId }),
  requestCreateGame: (gameName: string): RequestCreateGameAction => ({ type: types.REQUEST_CREATE_GAME, gameName }),
  requestStartGame: (): RequestStartGameAction => ({ type: types.REQUEST_START_GAME }),
  enterLobby: (gameId: number): EnterLobbyAction => ({ type: types.ENTER_LOBBY, gameId }),
  enterGame: (gameId: number): EnterGameAction => ({ type: types.ENTER_GAME, gameId }),
};
