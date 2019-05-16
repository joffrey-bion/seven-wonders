import { Button, Classes, Intent, NonIdealState } from '@blueprintjs/core';
import { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { ApiPlayer, ApiPlayerMove, ApiPlayerTurnInfo } from '../../api/model';
import { GlobalState } from '../../reducers';
import { actions } from '../../redux/actions/game';
import { getCurrentTurnInfo } from '../../redux/currentGame';
import { getCurrentGame } from '../../redux/games';
import { Board } from './Board';
import './GameScene.css'
import { Hand } from './Hand';
import { ProductionBar } from './ProductionBar';

type GameSceneStateProps = {
  players: List<ApiPlayer>,
  turnInfo: ApiPlayerTurnInfo | null,
}

type GameSceneDispatchProps = {
  sayReady: () => void,
  prepareMove: (move: ApiPlayerMove) => void,
}

type GameSceneProps = GameSceneStateProps & GameSceneDispatchProps

class GameScenePresenter extends Component<GameSceneProps> {

  render() {
    return (
      <div className='gameSceneRoot fullscreen'>
        {!this.props.turnInfo && <GamePreStart onReadyClicked={this.props.sayReady}/>}
        {this.props.turnInfo && this.turnInfoScene(this.props.turnInfo)}
      </div>
    );
  }

  turnInfoScene(turnInfo: ApiPlayerTurnInfo) {
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

type GamePreStartProps = {
  onReadyClicked: () => void
}
const GamePreStart = ({onReadyClicked}: GamePreStartProps) => <NonIdealState
        description={<p>Click "ready" when you are</p>}
        action={<Button text="READY" className={Classes.LARGE} intent={Intent.PRIMARY} icon='play'
                        onClick={() => onReadyClicked()}/>}
/>;

function mapStateToProps(state: GlobalState): GameSceneStateProps {
  const game = getCurrentGame(state);
  console.info(game);

  return {
    players: game ? List(game.players) : List(),
    turnInfo: getCurrentTurnInfo(state),
  };
}

function mapDispatchToProps(): GameSceneDispatchProps {
  return {
    sayReady: actions.sayReady,
    prepareMove: actions.prepareMove,
  }
}

export const GameScene = connect(mapStateToProps, mapDispatchToProps)(GameScenePresenter);
