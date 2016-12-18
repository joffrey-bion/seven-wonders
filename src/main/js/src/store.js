import { createStore, compose } from 'redux'

import createReducer from './reducers'

export default function configureStore(initialState = {}) {
    const middlewares = []

    const enhancers = [
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

    return store
}