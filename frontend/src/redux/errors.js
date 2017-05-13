import Immutable from 'seamless-immutable'

export const types = {
  ERROR_RECEIVED_ON_WS: 'ERROR/RECEIVED_ON_WS',
}

export const actions = {
  errorReceived: (error) => ({
    type: types.ERROR_RECEIVED_ON_WS,
    error
  })
}

const initialState = Immutable.from([])

export default (state = initialState, action) => {
  switch (action.type) {
    case types.ERROR_RECEIVED_ON_WS:
      return state.concat([ action.error ])
    default:
      return state
  }
}
