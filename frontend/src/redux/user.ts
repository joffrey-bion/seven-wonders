import { ApiPlayer } from '../api/model';
import { GlobalState } from '../reducers';
import { Action } from './actions/all';
import { SET_CURRENT_PLAYER } from './actions/user';
import { getCurrentGame } from './games';

export type User = {
  username: string,
  displayName: string,
}

export const currentUserReducer = (state: User | null = null, action: Action) => {
  switch (action.type) {
    case SET_CURRENT_PLAYER:
      return {
        username: action.player.username,
        displayName: action.player.displayName
      };
    default:
      return state;
  }
};

export function getCurrentUser(state: GlobalState): User | null {
  return state.currentUser
}

export function getCurrentPlayer(state: GlobalState): ApiPlayer | null {
  if (state.currentUser == null) {
    return null;
  }
  let game = getCurrentGame(state);
  if (game == null) {
    return null;
  }
  for (let i = 0; i < game.players.length; i++) {
    let player = game.players[i];
    if (player.username === state.currentUser.username) {
      return player;
    }
  }
  return null;
}
