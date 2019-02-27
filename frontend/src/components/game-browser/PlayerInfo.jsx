// @flow
import { Text } from '@blueprintjs/core';
import React from 'react';
import { connect } from 'react-redux';
import type { GlobalState } from '../../reducers';
import type { User } from '../../redux/user';
import { getCurrentUser } from '../../redux/user';

type PlayerInfoProps = {
  user: ?User,
}

const PlayerInfoPresenter = ({user}: PlayerInfoProps) => (
    <Text>
      <b>Username:</b>
      {' '}
      {user && user.displayName}
    </Text>
);

const mapStateToProps = (state: GlobalState): PlayerInfoProps => ({
  user: getCurrentUser(state),
});

const mapDispatchToProps = {
};

export const PlayerInfo = connect(mapStateToProps, mapDispatchToProps)(PlayerInfoPresenter);
