import { Map } from 'immutable'

export const types = {
  NEW_GAME: 'GAME/NEW_GAME',
  ENTER_GAME: 'GAME/ENTER_GAME',
  JOIN_GAME: 'GAME/JOIN_GAME',
  CREATE_GAME: 'GAME/CREATE_GAME',
}

export const actions = {
  newGame: (game) => ({ type: types.NEW_GAME, game }),
  enterGame: (username) => ({ type: types.ENTER_GAME, username }),
  joinGame: (id) => ({ type: types.JOIN_GAME, id }),
  createGame: (name) => ({ type: types.CREATE_GAME, name }),
}


const initialState = Map({})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.NEW_GAME:
      return state.set(action.game.get('id'), action.game)
    default:
      return state
  }
}
