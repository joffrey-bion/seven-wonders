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
  username: '',
  displayName: '',
  id: null
})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.SET_USERNAME:
      return state.set('username', action.username)
        .set('displayName', action.displayName)
        .set('id', action.index)
    default:
      return state
  }
}
