import { Map } from 'immutable';
import { PlayerShape } from '../../models/players';

export const types = {
  REQUEST_CHOOSE_USERNAME: 'USER/REQUEST_CHOOSE_USERNAME',
  SET_CURRENT_PLAYER: 'USER/SET_CURRENT_PLAYER',
  UPDATE_PLAYERS: 'USER/UPDATE_PLAYERS',
};

export type RequestChooseUsernameAction = { type: types.REQUEST_CHOOSE_USERNAME, username: string };
export type SetCurrentPlayerAction = { type: types.SET_CURRENT_PLAYER, player: PlayerShape };
export type UpdatePlayersAction = { type: types.UPDATE_PLAYERS, players: Map<string, PlayerShape> };

export type PlayerAction = RequestChooseUsernameAction | SetCurrentPlayerAction | UpdatePlayersAction;

export const actions = {
  chooseUsername: (username: string): RequestChooseUsernameAction => ({
    type: types.REQUEST_CHOOSE_USERNAME,
    username,
  }),
  setCurrentPlayer: (player: PlayerShape): SetCurrentPlayerAction => ({
    type: types.SET_CURRENT_PLAYER,
    player,
  }),
  updatePlayers: (players: Map<string, PlayerShape>): UpdatePlayersAction => ({
    type: types.UPDATE_PLAYERS,
    players,
  }),
};
