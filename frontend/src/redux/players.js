import { fromJS, Map } from 'immutable'

export const types = {
  SET_USERNAME: 'USER/SET_USERNAME',
  SET_USERNAMES: 'USER/SET_USERNAMES',
  CHOOSE_USERNAME: 'USER/CHOOSE_USERNAME'
}

export const actions = {
  setUsername: (username, displayName, index) => ({
    type: types.SET_USERNAME,
    username,
    index,
    displayName
  }),
  setPlayers: (players) => ({ type: types.SET_USERNAMES, players }),
  chooseUsername: (username) => ({ type: types.CHOOSE_USERNAME, username }),
}


const initialState = fromJS({
  all: {},
  current: ''
})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.SET_USERNAME:
      const user = fromJS({
        username: action.username,
        displayName: action.displayName,
        index: action.index,
      })
      return state.setIn(['all', user.get('username')], user).set('current', user.get('username'))
    case types.SET_USERNAMES:
      return state.setIn(['all'], state.get('all').mergeDeep(action.players))
    default:
      return state
  }
}

export const getCurrentPlayerUserName = state => state.get('players').get('current')
export const getAllPlayers = state => state.get('players').get('all')
export const getCurrentPlayer = (state) => getAllPlayers(state).get(getCurrentPlayerUserName(state), Map())
