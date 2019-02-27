// @flow
import { List, Map } from 'immutable';
import { combineReducers } from 'redux';
import type { ApiLobby } from '../api/model';
import type { GlobalState } from '../reducers';
import type { Action } from './actions/all';
import { types } from './actions/lobby';

export type GamesState = {
  all: Map<string, ApiLobby>,
  current: string | void
};

export const createGamesReducer = () => {
  return combineReducers({
    all: allGamesReducer,
    current: currentGameIdReducer
  })
};

export const allGamesReducer = (state: Map<string, ApiLobby> = Map(), action: Action) => {
  switch (action.type) {
    case types.UPDATE_GAMES:
      let newGames = {};
      action.games.forEach(g => newGames[g.id] = g);
      return state.merge(Map(newGames));
    default:
      return state;
  }
};

export const currentGameIdReducer = (state: string | void = null, action: Action) => {
  switch (action.type) {
    case types.ENTER_LOBBY:
      return `${action.gameId}`;
    default:
      return state;
  }
};

export const getAllGames = (state: GlobalState): List<ApiLobby> => state.games.all.toList();
export const getCurrentGame = (state: GlobalState): ApiLobby | null => state.games.all.get(state.games.current);
