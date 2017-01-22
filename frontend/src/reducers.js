import { combineReducers } from 'redux-immutable'

// react-router-redux immutable reducer
import { fromJS } from 'immutable'
import { LOCATION_CHANGE } from 'react-router-redux'

const initialState = fromJS({
  locationBeforeTransitions: null
})

const routerImmutableReducer = (state = initialState, action) => {
  if (action.type === LOCATION_CHANGE) {
    return state.set('locationBeforeTransitions', action.payload)
  }

  return state
}

import gamesReducer from './redux/games'
import playersReducer from './redux/players'

export default function createReducer() {
  return combineReducers({
    games: gamesReducer,
    routing: routerImmutableReducer,
    players: playersReducer,
  })
}
