// @flow
import { routerReducer } from 'react-router-redux';
import { combineReducers } from 'redux-immutable';
import { gamesReducer } from './redux/games';
import { playersReducer } from './redux/players';

export function createReducer() {
  return combineReducers({
    games: gamesReducer,
    players: playersReducer,
    routing: routerReducer,
  });
}
