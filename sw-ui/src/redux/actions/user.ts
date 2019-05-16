import { Map } from 'immutable';
import { ApiPlayer } from '../../api/model';

export const REQUEST_CHOOSE_USERNAME = 'USER/REQUEST_CHOOSE_USERNAME';
export const SET_CURRENT_PLAYER = 'USER/SET_CURRENT_PLAYER';
export const UPDATE_PLAYERS = 'USER/UPDATE_PLAYERS';

export type RequestChooseUsernameAction = { type: typeof REQUEST_CHOOSE_USERNAME, username: string };
export type SetCurrentPlayerAction = { type: typeof SET_CURRENT_PLAYER, player: ApiPlayer };
export type UpdatePlayersAction = { type: typeof UPDATE_PLAYERS, players: Map<string, ApiPlayer> };

export type PlayerAction = RequestChooseUsernameAction | SetCurrentPlayerAction | UpdatePlayersAction;

export const actions = {
  chooseUsername: (username: string): RequestChooseUsernameAction => ({ type: REQUEST_CHOOSE_USERNAME, username }),
  setCurrentPlayer: (player: ApiPlayer): SetCurrentPlayerAction => ({ type: SET_CURRENT_PLAYER, player }),
};
