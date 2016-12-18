import { createStore, compose } from 'redux'

import createReducer from './reducers'

const devtools = window.devToolsExtension || (() => noop => noop)

export default function configureStore(initialState = {}) {
    const middlewares = []

    const enhancers = [
        devtools()
    ]

    const store = createStore(
        createReducer(),
        initialState,
        compose(...enhancers)
    )

    return store
}