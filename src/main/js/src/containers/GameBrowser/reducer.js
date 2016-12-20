import { NEW_GAME } from './constants'

const initialState = {
}

export default function reducer(state = initialState, action) {
  switch (action.type) {
    case NEW_GAME:
      return {
        ...state,
        [action.game.id]: action.game
      }
    default:
      return state
  }
}
