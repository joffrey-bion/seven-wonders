import { Button, Classes } from '@blueprintjs/core'
import { List } from 'immutable';
import React from 'react';
import { connect } from 'react-redux';
import { ApiLobby } from '../../api/model';
import { GlobalState } from '../../reducers';
import { actions } from '../../redux/actions/lobby';
import { getAllGames } from '../../redux/games';
import './GameList.css';
import { GameStatus } from './GameStatus';
import { PlayerCount } from './PlayerCount';

type GameListStateProps = {
  games: List<ApiLobby>,
};

type GameListDispatchProps = {
  joinGame: (gameId: number) => void,
};

type GameListProps = GameListStateProps & GameListDispatchProps

const GameListPresenter = ({ games, joinGame }: GameListProps) => (
        <table className={Classes.HTML_TABLE}>
          <thead>
          <GameListHeaderRow />
          </thead>
          <tbody>
          {games.map((game: ApiLobby) => <GameListItemRow key={game.id} game={game} joinGame={joinGame}/>)}
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

type GameListItemRowProps = {
  game: ApiLobby,
  joinGame: (gameId: number) => void,
};

const GameListItemRow = ({game, joinGame}: GameListItemRowProps) => (
  <tr className="gameListRow">
    <td>{game.name}</td>
    <td>
      <GameStatus state={game.state} />
    </td>
    <td>
      <PlayerCount nbPlayers={game.players.length} />
    </td>
    <td>
      <JoinButton game={game} joinGame={joinGame}/>
    </td>
  </tr>
);

type JoinButtonProps = {
  game: ApiLobby,
  joinGame: (gameId: number) => void,
};

const JoinButton = ({game, joinGame}: JoinButtonProps) => {
  const disabled = game.state !== 'LOBBY';
  const onClick = () => joinGame(game.id);
  return <Button minimal disabled={disabled} icon='arrow-right' title='Join Game' onClick={onClick}/>;
};

function mapStateToProps(state: GlobalState): GameListStateProps {
  return {
    games: getAllGames(state),
  };
}

const mapDispatchToProps: GameListDispatchProps = {
  joinGame: actions.requestJoinGame,
};

export const GameList = connect(mapStateToProps, mapDispatchToProps)(GameListPresenter);

