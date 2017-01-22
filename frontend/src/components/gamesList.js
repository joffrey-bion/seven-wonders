import React from 'react'
import { Flex } from 'reflexbox'
import { Text, Space } from 'rebass'

const GameBrowser = (props) => (
  <div>
    {props.games.valueSeq().map((game, index) => {
      return (<Flex key={index}>
        <Text>{game.get('name')}</Text>
        <Space auto />
        <a href="#">Join</a>
      </Flex>)
    })}
  </div>
)

export default GameBrowser
