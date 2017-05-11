import React, { Component } from 'react'
import { connect } from 'react-redux'
import {
  Space,
  InlineForm,
  Text
} from 'rebass'
import { Flex } from 'reflexbox'
import GameList from '../components/gameList'

class GameBrowser extends Component {

  createGame = (e) => {
    e.preventDefault()
    if (this._gameName !== undefined) {
      this.props.createGame(this._gameName)
    }
  }

  render() {
    return (
      <div>
        <Flex align="center" p={1}>
          <InlineForm
            buttonLabel="Create Game"
            label="Game name"
            name="game_name"
            onChange={(e) => this._gameName = e.target.value}
            onClick={this.createGame}
          >
          </InlineForm>
          <Space auto />
          <Text><b>Username:</b> {this.props.currentPlayer.get('displayName')}</Text>
          <Space x={1} />
        </Flex>
        <GameList games={this.props.games} joinGame={this.props.joinGame}/>
      </div>
    )
  }
}

import { getCurrentPlayer } from '../redux/players'
import { getAllGames, actions } from '../redux/games'

const mapStateToProps = (state) => ({
  currentPlayer: getCurrentPlayer(state),
  games: getAllGames(state)
})

const mapDispatchToProps = {
  createGame: actions.requestCreateGame,
  joinGame: actions.requestJoinGame
}

export default connect(mapStateToProps, mapDispatchToProps)(GameBrowser)
