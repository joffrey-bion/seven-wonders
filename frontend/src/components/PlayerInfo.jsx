// @flow
import { Text } from '@blueprintjs/core';
import React from 'react';
import { connect } from 'react-redux';
import type { Player } from '../models/players';
import { getCurrentPlayer } from '../redux/players';

type PlayerInfoProps = {
  player: ?Player,
}

const PlayerInfoPresenter = ({player}: PlayerInfoProps) => (
    <Text>
      <b>Username:</b>
      {' '}
      {player && player.displayName}
    </Text>
);

const mapStateToProps = state => ({
  player: getCurrentPlayer(state),
});

const mapDispatchToProps = {
};

export const PlayerInfo = connect(mapStateToProps, mapDispatchToProps)(PlayerInfoPresenter);
