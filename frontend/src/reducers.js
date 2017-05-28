import { combineReducers } from 'redux-immutable';
import { routerReducer } from 'react-router-redux';
import { reducer as toastrReducer } from 'react-redux-toastr';

import gamesReducer from './redux/games';
import playersReducer from './redux/players';

export default function createReducer() {
  return combineReducers({
    games: gamesReducer,
    players: playersReducer,
    routing: routerReducer,
    toastr: toastrReducer,
  });
}
