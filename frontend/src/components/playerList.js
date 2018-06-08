//@flow
import { Text } from '@blueprintjs/core';
import { List } from 'immutable';
import React from 'react';
import { Flex } from 'reflexbox';
import { Player } from '../models/players';

export type PlayerListProps = {
  players: List<Player>;
};

export const PlayerList = ({ players }: PlayerListProps) => (
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
