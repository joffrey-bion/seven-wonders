import { List } from 'immutable';
import { Player, PlayerState } from '../models/players';
import type { Action } from './actions/all';
import { types } from './actions/players';

export const playersReducer = (state = new PlayerState(), action: Action) => {
  switch (action.type) {
    case types.SET_CURRENT_PLAYER:
      return state.addPlayer(action.player);
    case types.UPDATE_PLAYERS:
      return state.addPlayers(action.players);
    default:
      return state;
  }
};

const ANONYMOUS = new Player({displayName: '[NOT LOGGED]'});

export function getCurrentPlayer(state): Player {
  const players = state.get('players');
  return getPlayer(players, players.current, ANONYMOUS);
}

export const getPlayer = (players, username, defaultPlayer): ?Player => players.all.get(username, defaultPlayer);
export const getPlayers = (players, usernames): List<Player> => usernames.map(u => getPlayer(players, u, undefined));
