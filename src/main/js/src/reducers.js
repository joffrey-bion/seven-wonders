import { combineReducers } from 'redux'

import gamesReducer from './containers/GameBrowser/reducer'

export default function createReducer() {
  return combineReducers({
    games: gamesReducer,
  })
}
