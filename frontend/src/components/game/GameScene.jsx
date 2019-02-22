import { Button, Classes, Intent } from '@blueprintjs/core';
import { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import type { ApiPlayerTurnInfo } from '../../api/model';
import { Game } from '../../models/games';
import { Player } from '../../models/players';
import { actions } from '../../redux/actions/game';
import { getCurrentTurnInfo } from '../../redux/currentGame';
import { getCurrentGame } from '../../redux/games';

import { getCurrentPlayer, getPlayers } from '../../redux/players';
import { PlayerList } from '../lobby/PlayerList';

type GameSceneProps = {
  game: Game,
  currentPlayer: Player,
  players: List<Player>,
  turnInfo: ApiPlayerTurnInfo,
  sayReady: () => void
}

class GameScenePresenter extends Component<GameSceneProps> {
  getTitle() {
    if (this.props.game) {
      return this.props.game.name + ' — Game';
    } else {
      return 'What are you doing here? You haven\'t joined a game yet!';
    }
  }

  render() {
    return (
      <div>
        <h2>{this.getTitle()}</h2>
        <PlayerList players={this.props.players} currentPlayer={this.props.currentPlayer} owner={this.props.game.owner}/>
        <Button text="READY" className={Classes.LARGE} intent={Intent.PRIMARY} icon='play' onClick={this.props.sayReady} />

        <h3>Turn Info</h3>
        <div>
          <pre>{JSON.stringify(this.props.turnInfo, null, 2) }</pre>
        </div>
      </div>
    );
  }
}

const mapStateToProps: (state) => GameSceneProps = state => {
  const game = getCurrentGame(state.get('games'));
  console.info(game);

  return {
    game: game,
    currentPlayer: getCurrentPlayer(state),
    players: game ? getPlayers(state.get('players'), game.players) : new List(),
    turnInfo: getCurrentTurnInfo(state.get('currentGame'))
  };
};

const mapDispatchToProps = {
  sayReady: actions.sayReady,
};

export const GameScene = connect(mapStateToProps, mapDispatchToProps)(GameScenePresenter);
