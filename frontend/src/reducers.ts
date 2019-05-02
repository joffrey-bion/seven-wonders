// @flow
import { routerReducer } from 'react-router-redux';
import { combineReducers } from 'redux';
import { ApiPlayer } from './api/model';
import { CurrentGameState } from './redux/currentGame';
import { createCurrentGameReducer } from './redux/currentGame';
import { GamesState } from './redux/games';
import { createGamesReducer } from './redux/games';
import { currentUserReducer } from './redux/user';

export type GlobalState = {
  currentGame: CurrentGameState;
  currentUser: ApiPlayer | null;
  games: GamesState;
  routing: any;
}

export function createReducer() {
  return combineReducers({
    currentGame: createCurrentGameReducer(),
    currentUser: currentUserReducer,
    games: createGamesReducer(),
    routing: routerReducer,
  });
}
