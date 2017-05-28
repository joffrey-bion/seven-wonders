// @flow
export const makeSelectLocationState = () => {
  return state => {
    return state.get('routing');
  };
};
