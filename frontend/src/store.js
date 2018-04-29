// @flow
import { createStore, applyMiddleware, compose } from 'redux';
import createHistory from 'history/createBrowserHistory';
import { routerMiddleware } from 'react-router-redux';
import { fromJS } from 'immutable';
import createSagaMiddleware from 'redux-saga';
import { createReducer } from './reducers';
import { rootSaga } from './sagas';

export function configureStore(initialState: Object = {}) {
  const sagaMiddleware = createSagaMiddleware();

  const history = createHistory();

  const middlewares = [sagaMiddleware, routerMiddleware(history)];

  const enhancers = [applyMiddleware(...middlewares)];

  const composeEnhancers = process.env.NODE_ENV !== 'production' &&
    typeof window === 'object' &&
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    : compose;

  const store = createStore(createReducer(), fromJS(initialState), composeEnhancers(...enhancers));

  sagaMiddleware.run(rootSaga, history);

  return {
    store,
    history,
  };
}
