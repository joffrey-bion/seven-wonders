import Immutable from 'seamless-immutable'

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

const initialState = Immutable.from({
  all: {},
  current: ''
})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.SET_CURRENT_PLAYER:
      const player = action.player
      const withNewPlayer = state.setIn(['all', player.username], player)
      return Immutable.set(withNewPlayer, 'current', player.username)
    case types.UPDATE_PLAYERS:
      return Immutable.merge(state, {all: action.players}, {deep: true})
    default:
      return state
  }
}

export const getCurrentPlayer = state => state.players.all && state.players.all[state.players.current]
export const getPlayer = (state, username) => state.players.all[username]
export const getPlayers = (state, usernames) => usernames.map(u => getPlayer(state, u))
