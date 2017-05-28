import React from 'react';
import { Flex } from 'reflexbox';
import { Text, Space, Button } from 'rebass';

const GameList = ({ games, joinGame }) => (
  <div>
    {games.map((game, index) => {
      return (
        <Flex key={index}>
          <Text>{game.name}</Text>
          <Space auto />
          <Button onClick={() => joinGame(game.id)}>Join</Button>
        </Flex>
      );
    })}
  </div>
);

export default GameList;
