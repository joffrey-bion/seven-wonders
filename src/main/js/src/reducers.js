import { combineReducers } from 'redux'

import counterReducer from './containers/Counter/reducer'
export default function createReducer() {
    return combineReducers({
        counter: counterReducer
    })
}