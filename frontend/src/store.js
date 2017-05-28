// @flow
import { createStore, applyMiddleware, compose } from 'redux';
import { browserHistory } from 'react-router';
import { syncHistoryWithStore, routerMiddleware } from 'react-router-redux';
import { fromJS } from 'immutable';

import createReducer from './reducers';
import createSagaMiddleware from 'redux-saga';
import rootSaga from './sagas';
import { makeSelectLocationState } from './redux/app';

export default function configureStore(initialState: Object = {}) {
  const sagaMiddleware = createSagaMiddleware();

  const middlewares = [sagaMiddleware, routerMiddleware(browserHistory)];

  const enhancers = [applyMiddleware(...middlewares)];

  const composeEnhancers = process.env.NODE_ENV !== 'production' &&
    typeof window === 'object' &&
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    : compose;

  const store = createStore(createReducer(), fromJS(initialState), composeEnhancers(...enhancers));

  sagaMiddleware.run(rootSaga, browserHistory);

  return {
    store,
    history: syncHistoryWithStore(browserHistory, store, {
      selectLocationState: makeSelectLocationState(),
    }),
  };
}
