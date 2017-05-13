import React, { Component } from 'react'
import { connect } from 'react-redux'
import Immutable from 'seamless-immutable'
import { Text } from 'rebass'
import PlayerList from '../components/playerList'

class Lobby extends Component {

  getTitle() {
    if (this.props.currentGame) {
      return this.props.currentGame.name
    } else {
      return 'What are you doing here? You haven\'t joined a game yet!'
    }
  }

  render() {
    return (
      <div>
        <Text>{this.getTitle()}</Text>
        <PlayerList players={this.props.players}/>
      </div>
    )
  }
}

import { getPlayers } from '../redux/players'
import { getCurrentGame } from '../redux/games'

const mapStateToProps = (state) => {
  const game = getCurrentGame(state)
  return ({
    currentGame: game,
    players: game ? getPlayers(state, game.players) : Immutable([])
  })
}

const mapDispatchToProps = {}

export default connect(mapStateToProps, mapDispatchToProps)(Lobby)
