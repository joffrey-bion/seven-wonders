import ErrorsState from '../models/errors';

export const types = {
  ERROR_RECEIVED_ON_WS: 'ERROR/RECEIVED_ON_WS',
};

export const actions = {
  errorReceived: error => ({
    type: types.ERROR_RECEIVED_ON_WS,
    error,
  }),
};

export default (state = new ErrorsState(), action) => {
  switch (action.type) {
    case types.ERROR_RECEIVED_ON_WS:
      return state.addError(action.error);
    default:
      return state;
  }
};
