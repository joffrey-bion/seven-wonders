// @flow
import { Button, Text } from '@blueprintjs/core';
import type { List } from 'immutable';
import React from 'react';
import { connect } from 'react-redux';
import { Flex } from 'reflexbox';
import type { Game } from '../../models/games';
import { actions, getAllGames } from '../../redux/games';

type GameListProps = {
  games: List<Game>,
  joinGame: (gameId: string) => void,
};

const GameListPresenter = ({ games, joinGame }: GameListProps) => (
  <div>
    {games.map((game: Game, index: number) => {
      return (
        <Flex key={game.get('displayName', index)}>
          <Text>{game.name}</Text>
          <Button onClick={() => joinGame(game.id)}>Join</Button>
        </Flex>
      );
    })}
  </div>
);

const mapStateToProps = state => ({
  games: getAllGames(state.get('games')),
});

const mapDispatchToProps = {
  joinGame: actions.requestJoinGame,
};

export const GameList = connect(mapStateToProps, mapDispatchToProps)(GameListPresenter);

