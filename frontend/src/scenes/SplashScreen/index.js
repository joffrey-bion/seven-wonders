// @flow
import { Button, Classes, InputGroup, Intent } from '@blueprintjs/core';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { actions } from '../../redux/players';
import { HomeLayout } from './components/HomeLayout';

export type SplashScreenProps = {
  chooseUsername: (username: string) => void,
}

class SplashScreenPresenter extends Component<SplashScreenProps> {
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
        <form onSubmit={this.play}>
          <InputGroup
            placeholder="Username"
            onChange={e => (this._username = e.target.value)}
            rightElement={this.renderSubmit()}
          />
        </form>
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
