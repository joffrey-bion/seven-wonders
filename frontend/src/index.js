// @flow
import 'babel-polyfill';
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { ConnectedRouter } from 'react-router-redux';
import './global-styles.css';
import '@blueprintjs/core/dist/blueprint.css';

import { configureStore } from './store';
import { Application } from './scenes';

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
