// @flow
import React from 'react';
import { Flex } from 'reflexbox';
import { Text, Space, Button } from 'rebass';

import type { List } from 'immutable';
import type { Game } from '../models/games';

const GameList = ({ games, joinGame }: { games: List<Game>, joinGame: (gameId: string) => void }) => (
  <div>
    {games.map((game: Game, index: number) => {
      return (
        <Flex key={game.get('displayName', index)}>
          <Text>{game.name}</Text>
          <Space auto />
          <Button onClick={() => joinGame(game.id)}>Join</Button>
        </Flex>
      );
    })}
  </div>
);

export default GameList;
