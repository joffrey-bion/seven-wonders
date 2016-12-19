import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Flex } from 'reflexbox'
import { Text, Space } from 'rebass'

class GameBrowser extends Component {

  listGames = (games) => {
    return Object.keys(games).map(key => {
      const game = games[key]
      console.log('game', game, key)
      return (<Flex key={key}>
        <Text>{game.name}</Text>
        <Space auto />
        <a href="#">Join</a>
      </Flex>)
    })
  }

  render() {
    return (
      <div>
        {this.listGames(this.props.games)}
      </div>
    )
  }
}

const mapStateToProps = (state) => ({
  games: state.games
})

export default connect(mapStateToProps, {})(GameBrowser)
