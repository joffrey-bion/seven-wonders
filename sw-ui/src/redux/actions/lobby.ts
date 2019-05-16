import { ApiLobby } from '../../api/model';

export const UPDATE_GAMES = 'GAMES/UPDATE_GAMES';
export const REQUEST_CREATE_GAME = 'GAMES/REQUEST_CREATE_GAME';
export const REQUEST_JOIN_GAME = 'GAMES/REQUEST_JOIN_GAME';
export const REQUEST_START_GAME = 'GAMES/REQUEST_START_GAME';
export const ENTER_LOBBY = 'GAMES/ENTER_LOBBY';
export const ENTER_GAME = 'GAMES/ENTER_GAME';

export type UpdateGamesAction = { type: typeof UPDATE_GAMES, games: ApiLobby[]};
export type RequestCreateGameAction = { type: typeof REQUEST_CREATE_GAME, gameName: string };
export type RequestJoinGameAction = { type: typeof REQUEST_JOIN_GAME, gameId: number };
export type RequestStartGameAction = { type: typeof REQUEST_START_GAME };
export type EnterLobbyAction = { type: typeof ENTER_LOBBY, gameId: number };
export type EnterGameAction = { type: typeof ENTER_GAME, gameId: number };

export type LobbyAction =
        | UpdateGamesAction
        | RequestCreateGameAction
        | RequestJoinGameAction
        | RequestStartGameAction
        | EnterLobbyAction
        | EnterGameAction;

export const actions = {
  updateGames: (games: ApiLobby[]): UpdateGamesAction => ({ type: UPDATE_GAMES, games }),
  requestJoinGame: (gameId: number): RequestJoinGameAction => ({ type: REQUEST_JOIN_GAME, gameId }),
  requestCreateGame: (gameName: string): RequestCreateGameAction => ({ type: REQUEST_CREATE_GAME, gameName }),
  requestStartGame: (): RequestStartGameAction => ({ type: REQUEST_START_GAME }),
  enterLobby: (gameId: number): EnterLobbyAction => ({ type: ENTER_LOBBY, gameId }),
  enterGame: (gameId: number): EnterGameAction => ({ type: ENTER_GAME, gameId }),
};
