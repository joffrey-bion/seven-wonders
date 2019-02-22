// @flow
import { routerReducer } from 'react-router-redux';
import { combineReducers } from 'redux-immutable';
import { currentGameReducer } from './redux/currentGame';
import { gamesReducer } from './redux/games';
import { playersReducer } from './redux/players';

export function createReducer() {
  return combineReducers({
    currentGame: currentGameReducer,
    games: gamesReducer,
    players: playersReducer,
    routing: routerReducer,
  });
}
