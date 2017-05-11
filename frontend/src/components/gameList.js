import React from 'react'
import { Flex } from 'reflexbox'
import { Text, Space, Button } from 'rebass'

const GameList = (props) => (
  <div>
    {props.games.map((game, index) => {

      const joinGame = () => props.joinGame(game.get('id'))

      return (<Flex key={index}>
        <Text>{game.get('name')}</Text>
        <Space auto />
        <Button onClick={joinGame}>Join</Button>
      </Flex>)
    })}
  </div>
)

export default GameList
