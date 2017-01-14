import { SET_USERNAME } from './actions'
import { fromJS } from 'immutable'
const initialState = fromJS({
  username: '',
  displayName: '',
  id: null
})

export default (state = initialState, action) => {
  switch (action.type) {
    case SET_USERNAME:
      return state.set('username', action.userName)
        .set('displayName', action.displayName)
        .set('id', action.index)
    default:
      return state
  }
}
