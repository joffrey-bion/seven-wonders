import { createStore, applyMiddleware, compose } from 'redux'
import { browserHistory } from 'react-router'
import { syncHistoryWithStore, routerMiddleware } from 'react-router-redux'

import createReducer from './reducers'
import createSagaMiddleware from 'redux-saga'
import rootSaga from './sagas'

export default function configureStore(initialState = {}) {
  const sagaMiddleware = createSagaMiddleware()

  const middlewares = [
    sagaMiddleware,
    routerMiddleware(browserHistory)
  ]

  const enhancers = [
    applyMiddleware(...middlewares)
  ]

  const composeEnhancers =
    process.env.NODE_ENV !== 'production' &&
    typeof window === 'object' &&
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ ?
      window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ : compose;

  const store = createStore(
    createReducer(),
    initialState,
    composeEnhancers(...enhancers)
  )

  sagaMiddleware.run(rootSaga, browserHistory)

  return {
    store,
    history: syncHistoryWithStore(browserHistory, store)
  }
}
