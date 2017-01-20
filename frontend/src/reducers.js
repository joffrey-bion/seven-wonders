import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import gamesReducer from './redux/game'
import userReducer from './redux/user'

export default function createReducer() {
  return combineReducers({
    games: gamesReducer,
    routing: routerReducer,
    user: userReducer,
  })
}
