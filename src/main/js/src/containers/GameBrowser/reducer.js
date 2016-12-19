import { NEW_GAME } from './constants'

const initialState = {
  games: {}
}

export default function reducer(state = initialState, action) {
  switch (action.type) {
    case NEW_GAME:
      return {
        ...state,
        games: {
          ...state.games,
          [action.game.id]: action.game
        }
      }
    default:
      return state
  }
}
