import React from 'react';
import { Text } from 'rebass';
import { Flex } from 'reflexbox';

const PlayerList = ({ players }) => (
  <div>
    {players.map(player => {
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
