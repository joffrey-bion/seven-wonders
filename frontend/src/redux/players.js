import {fromJS, Map, Set} from 'immutable'

export const types = {
  REQUEST_CHOOSE_USERNAME: 'USER/REQUEST_CHOOSE_USERNAME',
  SET_CURRENT_PLAYER: 'USER/SET_CURRENT_PLAYER',
  UPDATE_PLAYERS: 'USER/UPDATE_PLAYERS'
}

export const actions = {
  chooseUsername: (username) => ({
    type: types.REQUEST_CHOOSE_USERNAME,
    username
  }),
  setCurrentPlayer: (player) => ({
    type: types.SET_CURRENT_PLAYER,
    player
  }),
  updatePlayers: (players) => ({
    type: types.UPDATE_PLAYERS,
    players
  }),
}

const initialState = fromJS({
  all: {},
  current: ''
})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.SET_CURRENT_PLAYER:
      const player = action.player
      const username = player.get('username')
      return state.setIn(['all', username], player).set('current', username)
    case types.UPDATE_PLAYERS:
      return state.setIn(['all'], state.get('all').mergeDeep(action.players))
    default:
      return state
  }
}

const getState = globalState => globalState.get('players')

function keyIn(...keys) {
  return (v, k) => Set(keys).has(k)
}

export const getAllPlayersByUsername = globalState => getState(globalState).get('all')
export const getAllPlayers = globalState => getAllPlayersByUsername(globalState).toList()
export const getPlayers = (globalState, usernames) => getAllPlayersByUsername(globalState)
  .filter(keyIn(usernames))
  .toList()
export const getCurrentPlayerUsername = globalState => getState(globalState).get('current')
export const getCurrentPlayer = globalState => getAllPlayersByUsername(globalState)
  .get(getCurrentPlayerUsername(globalState), Map())
