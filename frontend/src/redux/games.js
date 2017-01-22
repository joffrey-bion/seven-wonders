import { Map } from 'immutable'

export const types = {
  CREATE_OR_UPDATE_GAMES: 'GAME/CREATE_OR_UPDATE_GAMES',
  ENTER_GAME: 'GAME/ENTER_GAME',
  JOIN_GAME: 'GAME/JOIN_GAME',
  CREATE_GAME: 'GAME/CREATE_GAME',
}

export const actions = {
  createOrUpdateGame: (games) => ({ type: types.CREATE_OR_UPDATE_GAMES, games }),
  enterGame: (username) => ({ type: types.ENTER_GAME, username }),
  joinGame: (id) => ({ type: types.JOIN_GAME, id }),
  createGame: (name) => ({ type: types.CREATE_GAME, name }),
}


const initialState = Map({})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.NEW_GAME:
      return state.set(action.game.get('id'), action.game)
    case types.CREATE_OR_UPDATE_GAMES:
      return state.mergeDeep(action.games)
    default:
      return state
  }
}
