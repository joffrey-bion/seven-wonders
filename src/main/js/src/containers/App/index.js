import React from 'react'
import { Link } from 'react-router'
export default () => {
    return <div>
        <h1>Hello World</h1>
        <Link to="/counter">Go to counter +/-</Link>
        <br/>
        <Link to="/404">Go to 404</Link>
    </div>
}