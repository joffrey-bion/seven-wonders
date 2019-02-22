// @flow
import type { ApiPlayerTurnInfo } from '../api/model';
import { CurrentGameState } from '../models/currentGame';
import type { Action } from './actions/all';
import { types } from './actions/game';

export const currentGameReducer = (state: CurrentGameState = new CurrentGameState(), action: Action) => {
  switch (action.type) {
    case types.REQUEST_SAY_READY:
      // TODO handle end of feedback between say ready and ready event received
      return state;
    case types.PLAYER_READY_RECEIVED:
      // const newReadiness = state.playersReadiness.set(action.username, true);
      // return { playersReadiness: newReadiness, ...state };
      return state;
    case types.TABLE_UPDATE_RECEIVED:
      // TODO
      return state;
    case types.PREPARED_CARD_RECEIVED:
      // TODO
      return state;
    case types.TURN_INFO_RECEIVED:
      // TODO find a better way to just update what's needed
      const newState = new CurrentGameState();
      newState.turnInfo = action.turnInfo;
      newState.playersReadiness = state.playersReadiness;
      return newState;
    default:
      return state;
  }
};

export const getCurrentTurnInfo = (state: CurrentGameState): ApiPlayerTurnInfo => state.turnInfo;
