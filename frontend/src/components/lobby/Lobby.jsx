//@flow
import { Button, Classes, Intent } from '@blueprintjs/core';
import { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import type { ApiLobby, ApiPlayer } from '../../api/model';
import type { GlobalState } from '../../reducers';
import { actions } from '../../redux/actions/lobby';
import { getCurrentGame } from '../../redux/games';
import { getCurrentPlayer } from '../../redux/user';
import { RadialPlayerList } from './RadialPlayerList';

export type LobbyProps = {
  currentGame: ApiLobby,
  currentPlayer: ApiPlayer,
  players: List<ApiPlayer>,
  startGame: () => void,
}

class LobbyPresenter extends Component<LobbyProps> {

  render() {
    const {currentGame, currentPlayer, players, startGame} = this.props;
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

const mapStateToProps = (state: GlobalState) => {
  const game = getCurrentGame(state);
  console.info(game);
  return {
    currentGame: game,
    currentPlayer: getCurrentPlayer(state),
    players: game ? new List(game.players) : new List(),
  };
};

const mapDispatchToProps = {
  startGame: actions.requestStartGame,
};

export const Lobby = connect(mapStateToProps, mapDispatchToProps)(LobbyPresenter);
