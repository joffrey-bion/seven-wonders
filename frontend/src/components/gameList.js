import React from 'react'
import { Flex } from 'reflexbox'
import { Text, Space, Button } from 'rebass'
import Immutable from 'seamless-immutable'

const GameList = (props) => (
  <div>
    {Immutable.asMutable(props.games).map((game, index) => {

      const joinGame = () => props.joinGame(game.id)

      return (<Flex key={index}>
        <Text>{game.name}</Text>
        <Space auto />
        <Button onClick={joinGame}>Join</Button>
      </Flex>)
    })}
  </div>
)

export default GameList
