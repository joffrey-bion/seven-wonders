// @flow
import type { List } from 'immutable';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { InlineForm, Space, Text } from 'rebass';
import { Flex } from 'reflexbox';
import { GameList } from '../../components/gameList';
import type { Game } from '../../models/games';
import type { Player } from '../../models/players';
import { actions, getAllGames } from '../../redux/games';
import { getCurrentPlayer } from '../../redux/players';

class GameBrowserPresenter extends Component {
  props: {
    currentPlayer: Player,
    games: List<Game>,
    createGame: (gameName: string) => void,
    joinGame: (gameId: string) => void
  };

  _gameName: string | void = undefined;

  createGame = (e: SyntheticEvent): void => {
    e.preventDefault();
    if (this._gameName !== undefined) {
      this.props.createGame(this._gameName);
    }
  };

  render() {
    return (
      <div>
        <Flex align="center" p={1}>
          <InlineForm
            buttonLabel="Create Game"
            label="Game name"
            name="game_name"
            onChange={(e: SyntheticInputEvent) => (this._gameName = e.target.value)}
            onClick={this.createGame}
          />
          <Space auto />
          <Text>
            <b>Username:</b>
            {' '}
            {this.props.currentPlayer && this.props.currentPlayer.displayName}
          </Text>
          <Space x={1} />
        </Flex>
        <GameList games={this.props.games} joinGame={this.props.joinGame} />
      </div>
    );
  }
}

const mapStateToProps = state => ({
  currentPlayer: getCurrentPlayer(state.get('players')),
  games: getAllGames(state.get('games')),
});

const mapDispatchToProps = {
  createGame: actions.requestCreateGame,
  joinGame: actions.requestJoinGame,
};

export const GameBrowser = connect(mapStateToProps, mapDispatchToProps)(GameBrowserPresenter);
