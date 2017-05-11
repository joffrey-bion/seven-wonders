import React, { Component } from 'react'
import { connect } from 'react-redux'
import { List } from 'immutable'
import { Text } from 'rebass'
import PlayerList from '../components/playerList'

class Lobby extends Component {

  render() {
    return (
      <div>
        {this.props.currentGame && <Text>{this.props.currentGame.name}</Text>}
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
    players: game ? getPlayers(state, game.get('players')) : List()
  })
}

const mapDispatchToProps = {}

export default connect(mapStateToProps, mapDispatchToProps)(Lobby)
