// @flow
import 'babel-polyfill';
import React from 'react';
import ReactDOM from 'react-dom';
import { ConnectedRouter } from 'react-router-redux';
import { Route, Switch } from 'react-router';
import { Provider } from 'react-redux';
import './global-styles.css';

import configureStore from './store';
import HomePage from './containers/home';
const initialState = {};
const { store, history } = configureStore(initialState);

ReactDOM.render(
  <Provider store={store}>
    <ConnectedRouter history={history}>
      <Switch>
        <Route path="/" component={HomePage} />
      </Switch>
    </ConnectedRouter>
  </Provider>,
  document.getElementById('root')
);
