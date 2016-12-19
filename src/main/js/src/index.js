import 'babel-polyfill'

import React from 'react'
import ReactDOM from 'react-dom'
import { BrowserRouter, Match, Miss } from 'react-router'
import { Provider } from 'react-redux'
import configureStore from './store'

const initialState = {}
const store = configureStore(initialState)

import './global-styles.css'
import App from './containers/App'
import Counter from './containers/Counter'
import Error404 from './components/errors/Error404'

ReactDOM.render(
  <Provider store={store}>
    <BrowserRouter>
      <div className="app">
        <Match exactly pattern="/" component={App}/>
        <Match exactly pattern="/counter" component={Counter}/>
        <Miss component={Error404}/>
      </div>
    </BrowserRouter>
  </Provider>,
  document.getElementById('root')
);
