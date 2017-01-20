import { fromJS } from 'immutable'

export const types = {
  SET_USERNAME: 'USER/SET_USERNAME',
}

export const setUsername = (userName, displayName, index) => ({
  type: types.SET_USERNAME,
  userName,
  index,
  displayName
})

const initialState = fromJS({
  username: '',
  displayName: '',
  id: null
})

export default (state = initialState, action) => {
  switch (action.type) {
    case types.SET_USERNAME:
      return state.set('username', action.userName)
        .set('displayName', action.displayName)
        .set('id', action.index)
    default:
      return state
  }
}
