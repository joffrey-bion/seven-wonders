// @flow
import { Button, Classes, InputGroup, Intent } from '@blueprintjs/core';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Flex } from 'reflexbox';
import { actions } from '../../redux/actions/lobby';
import { GameList } from './GameList';
import { PlayerInfo } from './PlayerInfo';

type GameBrowserProps = {
  createGame: (gameName: string) => void,
}

class GameBrowserPresenter extends Component<GameBrowserProps> {

  _gameName: string | void = undefined;

  createGame = (e: SyntheticEvent<*>): void => {
    e.preventDefault();
    if (this._gameName !== undefined) {
      this.props.createGame(this._gameName);
    }
  };

  render() {
    return (
      <div>
        <Flex align="center" justify='space-between' p={1}>
          <form onSubmit={this.createGame}>
            <InputGroup
                    placeholder="Game name"
                    name="game_name"
                    onChange={(e: SyntheticInputEvent<*>) => (this._gameName = e.target.value)}
                    rightElement={<CreateGameButton onClick={this.createGame}/>}
            />
          </form>
          <PlayerInfo />
        </Flex>
        <GameList />
      </div>
    );
  }
}

const CreateGameButton = ({onClick}) => (
  <Button className={Classes.MINIMAL} intent={Intent.PRIMARY} icon='add' onClick={onClick} />
);

const mapDispatchToProps = {
  createGame: actions.requestCreateGame,
};

export const GameBrowser = connect(null, mapDispatchToProps)(GameBrowserPresenter);
