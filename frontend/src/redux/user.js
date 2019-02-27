import { ApiPlayer } from '../api/model';
import type { GlobalState } from '../reducers';
import type { Action } from './actions/all';
import { types } from './actions/user';
import { getCurrentGame } from './games';

export type User = {
  username: string,
  displayName: string,
}

export const currentUserReducer = (state: ?User = null, action: Action) => {
  switch (action.type) {
    case types.SET_CURRENT_PLAYER:
      return {
        username: action.player.username,
        displayName: action.player.displayName
      };
    default:
      return state;
  }
};

export function getCurrentUser(state: GlobalState): ?User {
  return state.currentUser
}

export function getCurrentPlayer(state: GlobalState): ApiPlayer {
  let game = getCurrentGame(state);
  for (let i = 0; i < game.players.length; i++) {
    let player = game.players[i];
    if (player.username === state.currentUser.username) {
      return player;
    }
  }
  return null;
}
