import '@blueprintjs/core/lib/css/blueprint.css';
import React from 'react';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { ConnectedRouter } from 'react-router-redux';
import { Application } from './components/Application';
import { INITIAL_STATE } from './reducers';
import { configureStore } from './store';

const { store, history } = configureStore(INITIAL_STATE);

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
