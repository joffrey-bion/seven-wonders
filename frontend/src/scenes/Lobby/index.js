import { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Button } from 'rebass';
import PlayerList from '../../components/playerList';
import { actions, getCurrentGame } from '../../redux/games';
import { getPlayers } from '../../redux/players';

class Lobby extends Component {
  getTitle() {
    if (this.props.currentGame) {
      return this.props.currentGame.name + ' — Lobby';
    } else {
      return 'What are you doing here? You haven\'t joined a game yet!';
    }
  }

  render() {
    return (
      <div>
        <h2>{this.getTitle()}</h2>
        <PlayerList players={this.props.players} />
        <Button onClick={this.props.startGame}>Start Game</Button>
      </div>
    );
  }
}

const mapStateToProps = state => {
  const game = getCurrentGame(state.get('games'));
  console.info(game);
  return {
    currentGame: game,
    players: game ? getPlayers(state.get('players'), game.players) : new List(),
  };
};

const mapDispatchToProps = {
  startGame: actions.requestStartGame,
};

export default connect(mapStateToProps, mapDispatchToProps)(Lobby);
