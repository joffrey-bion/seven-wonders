import { Button, Classes, InputGroup, Intent } from '@blueprintjs/core';
import React, { ChangeEvent, Component, SyntheticEvent } from 'react';
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

  createGame = (e: SyntheticEvent<any>): void => {
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
                    onChange={(e: ChangeEvent<HTMLInputElement>) => (this._gameName = e.target.value)}
                    rightElement={<CreateGameButton createGame={this.createGame}/>}
            />
          </form>
          <PlayerInfo />
        </Flex>
        <GameList />
      </div>
    );
  }
}

type CreateGameButtonProps = {
  createGame: (e: SyntheticEvent<any>) => void
}

const CreateGameButton = ({createGame}: CreateGameButtonProps) => (
  <Button className={Classes.MINIMAL} intent={Intent.PRIMARY} icon='add' onClick={createGame} />
);

const mapDispatchToProps = {
  createGame: actions.requestCreateGame,
};

export const GameBrowser = connect(null, mapDispatchToProps)(GameBrowserPresenter);
