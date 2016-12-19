import { combineReducers } from 'redux'

import counterReducer from './containers/Counter/reducer'
import gamesReducer from './containers/GameBrowser/reducer'

export default function createReducer() {
  return combineReducers({
    counter: counterReducer,
    games: gamesReducer,
  })
}
