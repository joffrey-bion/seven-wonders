import React from 'react';
import { Flex } from 'reflexbox';
import { Text } from 'rebass';
import Immutable from 'seamless-immutable';

const PlayerList = props => (
  <div>
    {Immutable.asMutable(props.players).map(player => {
      return (
        <Flex key={player.index}>
          <Text>{player.displayName}</Text>
          <Text>({player.username})</Text>
        </Flex>
      );
    })}
  </div>
);

export default PlayerList;
