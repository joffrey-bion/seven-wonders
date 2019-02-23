// @flow
import { List } from 'immutable';
import { combineReducers } from 'redux';
import type { ApiPlayerTurnInfo, ApiTable } from '../api/model';
import { CurrentGameState } from '../models/currentGame';
import type { Action } from './actions/all';
import { types } from './actions/game';

export function createCurrentGameReducer() {
  return combineReducers({
    readyUsernames: readyUsernamesReducer,
    turnInfo: turnInfoReducer,
  });
}

const readyUsernamesReducer = (state: List<string> = new List(), action: Action) => {
  if (action.type === types.PLAYER_READY_RECEIVED) {
    return state.push(action.username);
  } else {
    return state;
  }
};

const turnInfoReducer = (state: ApiPlayerTurnInfo | null = null, action: Action) => {
  if (action.type === types.TURN_INFO_RECEIVED) {
    return action.turnInfo;
  } else {
    return state;
  }
};

const tableUpdatesReducer = (state: ApiTable, action: Action) => {
  if (action.type === types.TABLE_UPDATE_RECEIVED) {
    return action.table;
  } else {
    return state;
  }
};

export const getCurrentTurnInfo = (state: CurrentGameState): ApiPlayerTurnInfo => state.turnInfo;
