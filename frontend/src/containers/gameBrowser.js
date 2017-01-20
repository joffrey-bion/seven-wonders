import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Flex } from 'reflexbox'
import { Text, Space } from 'rebass'

class GameBrowser extends Component {

  listGames = (games) => {
    return games.valueSeq().map((game, index) => {
      return (<Flex key={index}>
        <Text>{game.get('name')}</Text>
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
