import { Button, Classes, Intent } from '@blueprintjs/core';
import { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { ApiLobby, ApiPlayer } from '../../api/model';
import { GlobalState } from '../../reducers';
import { actions } from '../../redux/actions/lobby';
import { getCurrentGame } from '../../redux/games';
import { getCurrentPlayer } from '../../redux/user';
import { RadialPlayerList } from './RadialPlayerList';

export type LobbyStateProps = {
  currentGame: ApiLobby | null,
  currentPlayer: ApiPlayer | null,
  players: List<ApiPlayer>,
}

export type LobbyDispatchProps = {
  startGame: () => void,
}

export type LobbyProps = LobbyStateProps & LobbyDispatchProps

class LobbyPresenter extends Component<LobbyProps> {

  render() {
    const {currentGame, currentPlayer, players, startGame} = this.props;
    if (!currentGame || !currentPlayer) {
      return <div>Error: no current game.</div>
    }
    return (
      <div>
        <h2>{currentGame.name + ' — Lobby'}</h2>
        <RadialPlayerList players={players}/>
        {currentPlayer.gameOwner && <Button text="START" className={Classes.LARGE} intent={Intent.PRIMARY} icon='play'
                onClick={startGame} disabled={players.size < 3}/>}
      </div>
    );
  }
}

function mapStateToProps(state: GlobalState): LobbyStateProps {
  const game = getCurrentGame(state);
  console.info(game);
  return {
    currentGame: game,
    currentPlayer: getCurrentPlayer(state),
    players: game ? List(game.players) : List(),
  };
}

const mapDispatchToProps = {
  startGame: actions.requestStartGame,
};

export const Lobby = connect(mapStateToProps, mapDispatchToProps)(LobbyPresenter);
