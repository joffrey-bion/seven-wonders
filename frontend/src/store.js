// @flow
import createHistory from 'history/createBrowserHistory';
import { routerMiddleware } from 'react-router-redux';
import { applyMiddleware, createStore } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import createSagaMiddleware from 'redux-saga';
import type { GlobalState } from './reducers';
import { createReducer } from './reducers';
import { rootSaga } from './sagas';

export function configureStore(initialState: GlobalState = {}) {
  const sagaMiddleware = createSagaMiddleware();

  const history = createHistory();

  const middlewares = [sagaMiddleware, routerMiddleware(history)];

  const enhancers = [applyMiddleware(...middlewares)];

  const store = createStore(createReducer(), initialState, composeWithDevTools(...enhancers));

  sagaMiddleware.run(rootSaga);

  return {
    store,
    history,
  };
}
