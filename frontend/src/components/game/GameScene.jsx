import { Button, Classes, Intent, NonIdealState } from '@blueprintjs/core';
import { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import type { ApiPlayer, ApiPlayerMove, ApiPlayerTurnInfo } from '../../api/model';
import type { GlobalState } from '../../reducers';
import { actions } from '../../redux/actions/game';
import { getCurrentTurnInfo } from '../../redux/currentGame';
import { getCurrentGame } from '../../redux/games';
import { Board } from './Board';
import './GameScene.css'
import { Hand } from './Hand';
import { ProductionBar } from './ProductionBar';

type GameSceneProps = {
  players: List<ApiPlayer>,
  turnInfo: ApiPlayerTurnInfo,
  sayReady: () => void,
  prepareMove: (move: ApiPlayerMove) => void,
}

class GameScenePresenter extends Component<GameSceneProps> {

  render() {
    return (
      <div className='gameSceneRoot fullscreen'>
        {!this.props.turnInfo && <GamePreStart onReadyClicked={this.props.sayReady}/>}
        {this.props.turnInfo && this.turnInfoScene()}
      </div>
    );
  }

  turnInfoScene() {
    let turnInfo = this.props.turnInfo;
    let board = turnInfo.table.boards[turnInfo.playerIndex];
    return <div>
      <p>{turnInfo.message}</p>
      <Board board={board}/>
      <Hand cards={turnInfo.hand}
            wonderUpgradable={turnInfo.wonderBuildability.buildable}
            prepareMove={this.props.prepareMove}/>
      <ProductionBar gold={board.gold} production={board.production}/>
    </div>
  }
}

const GamePreStart = ({onReadyClicked}) => <NonIdealState
        description={<p>Click "ready" when you are</p>}
        action={<Button text="READY" className={Classes.LARGE} intent={Intent.PRIMARY} icon='play'
                        onClick={onReadyClicked}/>}
/>;

const mapStateToProps: (state: GlobalState) => GameSceneProps = state => {
  const game = getCurrentGame(state);
  console.info(game);

  return {
    players: game ? new List(game.players) : new List(),
    turnInfo: getCurrentTurnInfo(state),
  };
};

const mapDispatchToProps = {
  sayReady: actions.sayReady,
  prepareMove: actions.prepareMove,
};

export const GameScene = connect(mapStateToProps, mapDispatchToProps)(GameScenePresenter);
