import { Map } from 'immutable';
import type { ApiPlayer } from '../../api/model';

export const types = {
  REQUEST_CHOOSE_USERNAME: 'USER/REQUEST_CHOOSE_USERNAME',
  SET_CURRENT_PLAYER: 'USER/SET_CURRENT_PLAYER',
  UPDATE_PLAYERS: 'USER/UPDATE_PLAYERS',
};

export type RequestChooseUsernameAction = { type: types.REQUEST_CHOOSE_USERNAME, username: string };
export type SetCurrentPlayerAction = { type: types.SET_CURRENT_PLAYER, player: ApiPlayer };
export type UpdatePlayersAction = { type: types.UPDATE_PLAYERS, players: Map<string, ApiPlayer> };

export type PlayerAction = RequestChooseUsernameAction | SetCurrentPlayerAction | UpdatePlayersAction;

export const actions = {
  chooseUsername: (username: string): RequestChooseUsernameAction => ({
    type: types.REQUEST_CHOOSE_USERNAME,
    username,
  }),
  setCurrentPlayer: (player: ApiPlayer): SetCurrentPlayerAction => ({
    type: types.SET_CURRENT_PLAYER,
    player: player,
  }),
};
