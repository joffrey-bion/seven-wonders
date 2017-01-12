import { SET_USERNAME } from './actions'
import { fromJS } from 'immutable'
const initialState = fromJS({
  username: ''
})

export default (state = initialState, action) => {
  switch (action.type) {
    case SET_USERNAME:
      return state.set('username', action.username)
    default:
      return state
  }
}
