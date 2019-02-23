// @flow
import { routerReducer } from 'react-router-redux';
import { combineReducers } from 'redux-immutable';
import { createCurrentGameReducer } from './redux/currentGame';
import { gamesReducer } from './redux/games';
import { playersReducer } from './redux/players';

export function createReducer() {
  return combineReducers({
    currentGame: createCurrentGameReducer(),
    games: gamesReducer,
    players: playersReducer,
    routing: routerReducer,
  });
}
