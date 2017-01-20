import { combineReducers } from 'redux'
import { routerReducer } from 'react-router-redux'

import gamesReducer from './containers/GameBrowser/reducer'
import userReducer from './containers/UserRepo/reducer'

export default function createReducer() {
  return combineReducers({
    games: gamesReducer,
    routing: routerReducer,
    user: userReducer,
  })
}
