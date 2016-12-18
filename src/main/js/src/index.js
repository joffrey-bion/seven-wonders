import React from 'react'
import ReactDOM from 'react-dom'
import { Provider } from 'react-redux'
import configureStore from './store'
import './index.css'

const initialState = {}
const store = configureStore(initialState)

if (window.devToolsExtension) {
    window.devToolsExtension.updateStore(store)
}

import App from './App'

ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>,
  document.getElementById('root')
);
