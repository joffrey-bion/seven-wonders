import Immutable from 'seamless-immutable';

export const types = {
  ERROR_RECEIVED_ON_WS: 'ERROR/RECEIVED_ON_WS',
};

export const actions = {
  errorReceived: error => ({
    type: types.ERROR_RECEIVED_ON_WS,
    error,
  }),
};

const initialState = Immutable.from({
  nextId: 0,
  history: [],
});

export default (state = initialState, action) => {
  switch (action.type) {
    case types.ERROR_RECEIVED_ON_WS:
      let error = Object.assign(
        { id: state.nextId, timestamp: new Date() },
        action.error
      );
      let newState = state.set('nextId', state.nextId + 1);
      newState = addErrorToHistory(newState, error);
      return newState;
    default:
      return state;
  }
};

function addErrorToHistory(state, error) {
  return addToArray(state, 'history', error);
}

function addToArray(state, arrayKey, element) {
  return state.set(arrayKey, state[arrayKey].concat([element]));
}
