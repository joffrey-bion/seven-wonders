import { List, Map } from 'immutable';
import { combineReducers } from 'redux';
import { ApiLobby } from '../api/model';
import { GlobalState } from '../reducers';
import { Action } from './actions/all';
import { ENTER_LOBBY, UPDATE_GAMES } from './actions/lobby';

export type GamesState = {
  all: Map<string, ApiLobby>,
  current: string | null
};

export const EMPTY_GAMES: GamesState = {
  all: Map(),
  current: null,
};

export const createGamesReducer = () => {
  return combineReducers({
    all: allGamesReducer,
    current: currentGameIdReducer
  })
};

export const allGamesReducer = (state: Map<string, ApiLobby> = Map(), action: Action) => {
  switch (action.type) {
    case UPDATE_GAMES:
      const newGames = mapify(action.games);
      return state.merge(newGames);
    default:
      return state;
  }
};

function mapify(games: ApiLobby[]): Map<string, ApiLobby> {
  let newGames: {[id:string]:ApiLobby} = {};
  games.forEach(g => newGames[`${g.id}`] = g);
  return Map(newGames);
}

export const currentGameIdReducer = (state: string | null = null, action: Action) => {
  switch (action.type) {
    case ENTER_LOBBY:
      return `${action.gameId}`;
    default:
      return state;
  }
};

export const getAllGames = (state: GlobalState): List<ApiLobby> => state.games.all.toList();
export const getCurrentGame = (state: GlobalState): ApiLobby | null => {
  if (state.games.current == null) {
    return null;
  }
  return state.games.all.get(state.games.current) || null;
};
