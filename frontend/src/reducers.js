import { combineReducers, routerReducer } from 'redux-seamless-immutable'

import errorsReducer from './redux/errors'
import gamesReducer from './redux/games'
import playersReducer from './redux/players'

export default function createReducer() {
  return combineReducers({
    errors: errorsReducer,
    games: gamesReducer,
    players: playersReducer,
    routing: routerReducer,
  })
}
