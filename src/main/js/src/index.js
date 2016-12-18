import React from 'react'
import ReactDOM from 'react-dom'
import { BrowserRouter, Match, Miss } from 'react-router'
import { Provider } from 'react-redux'
import configureStore from './store'

const initialState = {}
const store = configureStore(initialState)

import './global-styles.css'
import App from './containers/App'
import Error404 from './components/errors/Error404'
ReactDOM.render(
  <Provider store={store}>
      <BrowserRouter>
          <div className="app">
              <Match exactly pattern="/" component={App} />
              <Miss component={Error404} />
          </div>
      </BrowserRouter>
  </Provider>,
  document.getElementById('root')
);
