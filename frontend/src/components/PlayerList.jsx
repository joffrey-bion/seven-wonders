//@flow
import { Text } from '@blueprintjs/core';
import { List } from 'immutable';
import React from 'react';
import { Flex } from 'reflexbox';
import { Player } from '../models/players';

type PlayerListProps = {
  players: List<Player>;
};

const PlayerItem = ({player}) => (
  <Flex>
    <Text>{player.displayName}</Text>
    <Text>({player.username})</Text>
  </Flex>
);

export const PlayerList = ({players}: PlayerListProps) => (
  <div>
    {players.map(player => <PlayerItem key={player.username} player={player}/>)}
  </div>
);
