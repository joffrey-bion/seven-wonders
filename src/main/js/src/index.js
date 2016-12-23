import 'babel-polyfill'

import React from 'react'
import ReactDOM from 'react-dom'
import { Router, Route } from 'react-router'
import { Provider } from 'react-redux'
import configureStore from './store'

const initialState = {}
const { store, history } = configureStore(initialState)

import './global-styles.css'
import App from './containers/App'
import Error404 from './components/errors/Error404'

ReactDOM.render(
  <Provider store={store}>
    <Router history={history}>
      <div className="app">
        <Route path="/" component={App}/>
        <Route path="*" component={Error404}/>
      </div>
    </Router>
  </Provider>,
  document.getElementById('root')
);
