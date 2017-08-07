import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Button, Container, Input } from 'rebass';
import { actions } from '../../redux/players';

import HomeLayout from './components/HomeLayout';

class SplashScreen extends Component {
  play = e => {
    if (this._username !== undefined) {
      this.props.chooseUsername(this._username);
    }
  };

  render() {
    return (
      <HomeLayout>
        <Container>
          <Input
            name="username"
            label="Username"
            placeholder="Username"
            hideLabel
            onChange={e => (this._username = e.target.value)}
          />
          <Button backgroundColor="primary" color="white" big onClick={this.play}>
            PLAY NOW!
          </Button>
        </Container>
      </HomeLayout>
    );
  }
}

const mapStateToProps = state => ({});

const mapDispatchToProps = {
  chooseUsername: actions.chooseUsername,
};

export default connect(mapStateToProps, mapDispatchToProps)(SplashScreen);
