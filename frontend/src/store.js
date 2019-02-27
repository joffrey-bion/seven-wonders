// @flow
import createHistory from 'history/createBrowserHistory';
import { routerMiddleware } from 'react-router-redux';
import { applyMiddleware, compose, createStore } from 'redux';
import createSagaMiddleware from 'redux-saga';
import type { GlobalState } from './reducers';
import { createReducer } from './reducers';
import { rootSaga } from './sagas';

export function configureStore(initialState: GlobalState = {}) {
  const sagaMiddleware = createSagaMiddleware();

  const history = createHistory();

  const middlewares = [sagaMiddleware, routerMiddleware(history)];

  const enhancers = [applyMiddleware(...middlewares)];

  const composeEnhancers = process.env.NODE_ENV !== 'production' &&
    typeof window === 'object' &&
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    : compose;

  const store = createStore(createReducer(), initialState, composeEnhancers(...enhancers));

  sagaMiddleware.run(rootSaga);

  return {
    store,
    history,
  };
}
