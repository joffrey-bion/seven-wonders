import {
    INCREMENT, DECREMENT,
    INCREMENT_IF_ODD, INCREMENT_ASYNC
} from './constants'

export const increment = () => ({
    type: INCREMENT
})

export const decrement = () => ({
    type: DECREMENT
})

export const incrementIfOdd = () => ({
    type: INCREMENT_IF_ODD
})

export const incrementAsync = () => ({
    type: INCREMENT_ASYNC
})