// @flow
import { Button, Icon } from '@blueprintjs/core';
import type { List } from 'immutable';
import React from 'react';
import { connect } from 'react-redux';
import type { Game } from '../../models/games';
import { actions, getAllGames } from '../../redux/games';
import './GameList.css';
import { GameStatus } from './GameStatus';
import { PlayerCount } from './PlayerCount';

type GameListProps = {
  games: List<Game>,
  joinGame: (gameId: string) => void,
};

const GameListPresenter = ({ games, joinGame }: GameListProps) => (
        <table className='pt-html-table pt-interactive'>
          <thead>
          <GameListHeaderRow />
          </thead>
          <tbody>
          {games.map((game: Game) => <GameListItemRow key={game.id} game={game} joinGame={joinGame}/>)}
          </tbody>
        </table>
);

const GameListHeaderRow = () => (
  <tr>
    <th>Name</th>
    <th>Status</th>
    <th>Nb Players</th>
    <th>Join</th>
  </tr>
);

const GameListItemRow = ({game, joinGame}) => (
  <tr>
    <td>{game.name}</td>
    <td>
      <GameStatus state={game.state} />
    </td>
    <td>
      <PlayerCount nbPlayers={game.players.size} />
    </td>
    <td>
      <JoinButton game={game} joinGame={joinGame}/>
    </td>
  </tr>
);

const JoinButton = ({game, joinGame}) => {
  const disabled = game.state !== 'LOBBY';
  const icon = <Icon icon='arrow-right' title={false} />;
  return <Button minimal disabled={disabled} icon={icon} title='Join Game' onClick={() => joinGame(game.id)}/>;
};

const mapStateToProps = state => ({
  games: getAllGames(state.get('games')),
});

const mapDispatchToProps = {
  joinGame: actions.requestJoinGame,
};

export const GameList = connect(mapStateToProps, mapDispatchToProps)(GameListPresenter);

