// @flow
import { combineReducers } from 'redux';
import type { ApiPlayerTurnInfo, ApiTable } from '../api/model';
import type { GlobalState } from '../reducers';
import type { Action } from './actions/all';
import { TABLE_UPDATE_RECEIVED, TURN_INFO_RECEIVED } from './actions/game';

export type CurrentGameState = {
  turnInfo: ApiPlayerTurnInfo | null;
  table: ApiTable | null;
}

export function createCurrentGameReducer() {
  return combineReducers({
    turnInfo: turnInfoReducer,
    table: tableUpdatesReducer,
  });
}

const turnInfoReducer = (state: ApiPlayerTurnInfo | null = null, action: Action) => {
  switch (action.type) {
    case TURN_INFO_RECEIVED:
      return action.turnInfo;
    case TABLE_UPDATE_RECEIVED:
      return null;
    default:
      return state;
  }
};

const tableUpdatesReducer = (state: ApiTable | null = null, action: Action) => {
  switch (action.type) {
    case TURN_INFO_RECEIVED:
      return action.turnInfo.table;
    case TABLE_UPDATE_RECEIVED:
      return action.table;
    default:
      return state;
  }
};

export const getCurrentTurnInfo = (state: GlobalState): ApiPlayerTurnInfo => state.currentGame.turnInfo;
