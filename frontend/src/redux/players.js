import { fromJS } from 'immutable'

export const types = {
  SET_USERNAME: 'USER/SET_USERNAME',
  CHOOSE_USERNAME: 'USER/CHOOSE_USERNAME'
}

export const actions = {
  setUsername: (username, displayName, index) => ({
    type: types.SET_USERNAME,
    username,
    index,
    displayName
  }),
  chooseUsername: (username) => ({ type: types.CHOOSE_USERNAME, username }),
}


const initialState = fromJS({
  all: {},
  current: null
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
    default:
      return state
  }
}
