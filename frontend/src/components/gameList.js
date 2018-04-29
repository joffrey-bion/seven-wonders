// @flow
import type { List } from 'immutable';
import React from 'react';
import { Button, Space, Text } from 'rebass';
import { Flex } from 'reflexbox';
import type { Game } from '../models/games';

export const GameList = ({ games, joinGame }: { games: List<Game>, joinGame: (gameId: string) => void }) => (
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
