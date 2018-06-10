// @flow
import '@blueprintjs/core/lib/css/blueprint.css';
import 'babel-polyfill';
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { ConnectedRouter } from 'react-router-redux';
import { Application } from './components/Application';
import { configureStore } from './store';

const initialState = {};
const { store, history } = configureStore(initialState);

const rootElement = document.getElementById('root');
if (rootElement) {
  ReactDOM.render(<Provider store={store}>
    <ConnectedRouter history={history}>
      <Application/>
    </ConnectedRouter>
  </Provider>, rootElement);
} else {
  console.error('Element with ID "root" was not found, cannot bootstrap react app');
}
