import React, { PropTypes } from 'react'
import { connect } from 'react-redux'

import {
    increment,
    decrement,
    incrementAsync,
    incrementIfOdd
} from './actions'

const Counter = ({ value, increment, decrement, incrementIfOdd, incrementAsync }) =>
        <p>
            Clicked: {value} times
            {' '}
            <button onClick={increment}>
                +
            </button>
            {' '}
            <button onClick={decrement}>
                -
            </button>
            {' '}
            <button onClick={incrementIfOdd}>
                Increment if odd
            </button>
            {' '}
            <button onClick={incrementAsync}>
                Increment async
            </button>
        </p>

Counter.propTypes = {
    value: PropTypes.number.isRequired,
    increment: PropTypes.func.isRequired,
    decrement: PropTypes.func.isRequired,
    incrementAsync: PropTypes.func.isRequired,
    incrementIfOdd: PropTypes.func.isRequired
}

const mapStateToProps = (state) => ({
    value: state.counter
})

const mapDispatchToProps = {
    increment,
    decrement,
    incrementAsync,
    incrementIfOdd
}

export default connect(mapStateToProps, mapDispatchToProps)(Counter)