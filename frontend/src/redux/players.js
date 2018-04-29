import { Map } from 'immutable';
import { Player, PlayerShape, PlayerState } from '../models/players';

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

export const playersReducer = (state = new PlayerState(), action: PlayerAction) => {
  switch (action.type) {
    case types.SET_CURRENT_PLAYER:
      return state.addPlayer(action.player);
    case types.UPDATE_PLAYERS:
      return state.addPlayers(action.players);
    default:
      return state;
  }
};

export const getCurrentPlayer = players => players.all.get(players.current, new Player({displayName: '[ERROR]'}));
export const getPlayer = (players, username) => players.all.get(username);
export const getPlayers = (players, usernames) => usernames.map(u => getPlayer(players, u));
