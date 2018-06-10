// @flow
import { Classes, InputGroup, Intent } from '@blueprintjs/core';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { actions } from '../../redux/players';
import { IconButton } from '../shared/IconButton';

export type ChooseNameFormPresenterProps = {
  chooseUsername: (username: string) => void,
}

class ChooseNameFormPresenter extends Component<ChooseNameFormPresenterProps> {
  _username = '';

  play = e => {
    e.preventDefault();
    if (this._username !== undefined) {
      this.props.chooseUsername(this._username);
    }
  };

  render() {
    return (
        <form onSubmit={this.play}>
          <InputGroup
            placeholder="Username"
            onChange={e => (this._username = e.target.value)}
            rightElement={this.renderSubmit()}
          />
        </form>
    );
  }

  renderSubmit = () => (
    <IconButton className={Classes.MINIMAL} onClick={this.play} intent={Intent.PRIMARY} icon="arrow-right" />
  );
}

const mapDispatchToProps = {
  chooseUsername: actions.chooseUsername,
};

export const ChooseNameForm = connect(null, mapDispatchToProps)(ChooseNameFormPresenter);
