// @flow
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Container } from 'rebass';
import { actions } from '../../redux/players';

import { InputGroup, Button, Classes, Intent } from '@blueprintjs/core';
import { HomeLayout } from './components/HomeLayout';

class SplashScreenPresenter extends Component {
  _username = '';

  play = e => {
    e.preventDefault();
    if (this._username !== undefined) {
      this.props.chooseUsername(this._username);
    }
  };

  render() {
    return (
      <HomeLayout>
        <Container>
          <form onSubmit={this.play}>
            <InputGroup
              placeholder="Username"
              onChange={e => (this._username = e.target.value)}
              rightElement={this.renderSubmit()}
            />
          </form>
        </Container>
      </HomeLayout>
    );
  }

  renderSubmit = () => (
    <Button className={Classes.MINIMAL} onClick={this.play} intent={Intent.PRIMARY} rightIconName="arrow-right" />
  );
}

const mapDispatchToProps = {
  chooseUsername: actions.chooseUsername,
};

export const SplashScreen = connect(null, mapDispatchToProps)(SplashScreenPresenter);
