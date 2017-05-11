import React from 'react'
import { Flex } from 'reflexbox'
import { Text } from 'rebass'

const PlayerList = (props) => (
  <div>
    {props.players.map((player, index) => {
      return (<Flex key={index}>
        <Text>{player.get('displayName')}</Text>
        <Text>({player.get('username')})</Text>
      </Flex>)
    })}
  </div>
)

export default PlayerList
