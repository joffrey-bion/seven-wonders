import { Map } from 'immutable'
import { NEW_GAME } from './constants'

const initialState = Map({})

export default function reducer(state = initialState, action) {
  switch (action.type) {
    case NEW_GAME:
      return Map.set(action.game.id, action.game)
    default:
      return state
  }
}
