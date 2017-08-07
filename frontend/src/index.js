// @flow
import 'babel-polyfill';
import React from 'react';
import ReactDOM from 'react-dom';
import { ConnectedRouter } from 'react-router-redux';
import { Provider } from 'react-redux';
import './global-styles.css';
import '@blueprintjs/core/dist/blueprint.css';

import configureStore from './store';
import Routes from './scenes';
const initialState = {};
const { store, history } = configureStore(initialState);

ReactDOM.render(
  <Provider store={store}>
    <ConnectedRouter history={history}>
      <Routes />
    </ConnectedRouter>
  </Provider>,
  document.getElementById('root')
);
