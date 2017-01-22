import React, { Component } from 'react'
import { connect } from 'react-redux'
import {
  Space,
  InlineForm,
  Text
} from 'rebass'
import { Flex } from 'reflexbox'
import GamesList from '../components/gamesList'

class App extends Component {

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
          <Text><b>Username:</b> {this.props.username}</Text>
          <Space x={1} />
        </Flex>
        <GamesList games={this.props.games} />
      </div>
    )
  }
}

const mapStateToProps = (state) => ({
  username: state.players.get('all').get(state.players.get('current')).get('displayName'),
  games: state.games
})


import { actions } from '../redux/games'
const mapDispatchToProps = {
  createGame: actions.createGame
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
