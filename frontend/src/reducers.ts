import { routerReducer } from 'react-router-redux';
import { combineReducers } from 'redux';
import { ApiPlayer } from './api/model';
import { CurrentGameState, EMPTY_CURRENT_GAME } from './redux/currentGame';
import { createCurrentGameReducer } from './redux/currentGame';
import { EMPTY_GAMES, GamesState } from './redux/games';
import { createGamesReducer } from './redux/games';
import { currentUserReducer } from './redux/user';

export type GlobalState = {
  currentGame: CurrentGameState;
  currentUser: ApiPlayer | null;
  games: GamesState;
  routing: any;
}

export const INITIAL_STATE: GlobalState = {
  currentGame: EMPTY_CURRENT_GAME,
  currentUser: null,
  games: EMPTY_GAMES,
  routing: null,
};

export function createReducer() {
  return combineReducers({
    currentGame: createCurrentGameReducer(),
    currentUser: currentUserReducer,
    games: createGamesReducer(),
    routing: routerReducer,
  });
}
