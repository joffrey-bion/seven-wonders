import { Button, Classes, Intent } from '@blueprintjs/core';
import { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import type { ApiHandCard, ApiPlayerTurnInfo } from '../../api/model';
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
  sayReady: () => void
}

type GameSceneState = {
  selectedCard: ApiHandCard | void
}

class GameScenePresenter extends Component<GameSceneProps, GameSceneState> {

  state = {
    selectedCard: null
  };

  selectCard(c: ApiHandCard) {
    this.setState({selectedCard: c})
  }

  render() {
    return (
      <div className='gameSceneRoot fullscreen'>
        <h2>Now playing!</h2>
        <p>{this.props.turnInfo ? this.props.turnInfo.message : 'Click "ready" when you are'}</p>

        {this.props.turnInfo && <Hand cards={this.props.turnInfo.hand}
                                      selectedCard={this.state.selectedCard}
                                      onClick={(c) => this.selectCard(c)}/>}

        {!this.props.turnInfo && <Button text="READY" className={Classes.LARGE} intent={Intent.PRIMARY} icon='play' onClick={this.props.sayReady} />}

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
