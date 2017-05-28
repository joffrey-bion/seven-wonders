// @flow
import 'babel-polyfill';
import './global-styles.css';

import React from 'react';
import ReactDOM from 'react-dom';
import { Router } from 'react-router';
import { Provider } from 'react-redux';

import configureStore from './store';
import { routes } from './routes';
const initialState = {};
const { store, history } = configureStore(initialState);

ReactDOM.render(
  <Provider store={store}>
    <Router history={history} routes={routes} />
  </Provider>,
  document.getElementById('root')
);
