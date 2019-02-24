import { Button, Classes, Intent } from '@blueprintjs/core';
import { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import type { ApiPlayerMove, ApiPlayerTurnInfo } from '../../api/model';
import { Game } from '../../models/games';
import { Player } from '../../models/players';
import { actions } from '../../redux/actions/game';
import { getCurrentTurnInfo } from '../../redux/currentGame';
import { getCurrentGame } from '../../redux/games';
import { getCurrentPlayer, getPlayers } from '../../redux/players';
import { Hand } from './Hand';
import './GameScene.css'

type GameSceneProps = {
  game: Game,
  currentPlayer: Player,
  players: List<Player>,
  turnInfo: ApiPlayerTurnInfo,
  sayReady: () => void,
  prepareMove: (move: ApiPlayerMove) => void,
}

class GameScenePresenter extends Component<GameSceneProps> {

  render() {
    return (
      <div className='gameSceneRoot fullscreen'>
        <h1>{this.props.game.name}</h1>
        {!this.props.turnInfo && <GamePreStart onReadyClicked={this.props.sayReady}/>}
        {this.props.turnInfo && this.turnInfoScene()}

        <h4 style={{marginTop: '2rem'}}>Debug</h4>
        <div>
          <pre>{JSON.stringify(this.props.turnInfo, null, 2) }</pre>
        </div>
      </div>
    );
  }

  turnInfoScene() {
    return <div>
      <p>{this.props.turnInfo.message}</p>
      <Hand cards={this.props.turnInfo.hand}
            wonderUpgradable={this.props.turnInfo.wonderBuildability.buildable}
            prepareMove={this.props.prepareMove}/>
    </div>
  }
}

const GamePreStart = ({onReadyClicked}) => <div>
  <p>Click "ready" when you are</p>
  <Button text="READY" className={Classes.LARGE} intent={Intent.PRIMARY} icon='play' onClick={onReadyClicked} />
</div>;

const mapStateToProps: (state) => GameSceneProps = state => {
  const game = getCurrentGame(state.get('games'));
  console.info(game);

  return {
    game: game,
    currentPlayer: getCurrentPlayer(state),
    players: game ? getPlayers(state.get('players'), game.players) : new List(),
    turnInfo: getCurrentTurnInfo(state.get('currentGame')),
  };
};

const mapDispatchToProps = {
  sayReady: actions.sayReady,
  prepareMove: actions.prepareMove,
};

export const GameScene = connect(mapStateToProps, mapDispatchToProps)(GameScenePresenter);
