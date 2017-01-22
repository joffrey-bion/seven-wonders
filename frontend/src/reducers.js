import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import gamesReducer from './redux/game'
import playersReducer from './redux/players'

export default function createReducer() {
  return combineReducers({
    games: gamesReducer,
    routing: routerReducer,
    players: playersReducer,
  })
}
