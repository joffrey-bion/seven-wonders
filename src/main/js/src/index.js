import React from 'react'
import ReactDOM from 'react-dom'
import { BrowserRouter, Match, Miss } from 'react-router'
import { Provider } from 'react-redux'
import configureStore from './store'

const initialState = {}
const store = configureStore(initialState)

if (window.devToolsExtension) {
    window.devToolsExtension.updateStore(store)
}

import './index.css'
import App from './containers/App'

const NoMatch  = () => {
    return <h1>No Match</h1>
}

ReactDOM.render(
  <Provider store={store}>
      <BrowserRouter>
          <div className="app">
              <Match exactly pattern="/" component={App} />
              <Miss component={NoMatch} />
          </div>
      </BrowserRouter>
  </Provider>,
  document.getElementById('root')
);
