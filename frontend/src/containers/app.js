import React, { Component } from 'react'
import { connect } from 'react-redux'
import {
  Banner,
  Heading,
  Space,
  Button,
  InlineForm,
  Text
} from 'rebass'
import { Flex } from 'reflexbox'
import Modal from '../../components/modals/username'
import GameBrowser from './gameBrowser'

class App extends Component {
  state = {
    usernameModal: false,
  }

  componentDidMount() {

  }

  toggleModal = (key) => {
    return (e) => {
      const val = !this.state[key]
      this.setState({ [key]: val })
    }
  }

  createGame = (e) => {
    e.preventDefault()
    if (this._gameName !== undefined) {
      this.props.createGame(this._gameName)
    }
  }

  render() {
    return (
      <div>
        <Banner
          align="center"
          style={{minHeight: '30vh'}}
          backgroundImage="https://images.unsplash.com/photo-1431207446535-a9296cf995b1?dpr=1&auto=format&fit=crop&w=1199&h=799&q=80&cs=tinysrgb&crop="
        >
          <Heading level={1}>Seven Wonders</Heading>
        </Banner>
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
          <Text><b>Username:</b> Cesar92</Text>
          <Space x={1} />
          <Button
            onClick={this.toggleModal('usernameModal')}
            children="Change"/>
        </Flex>
        <GameBrowser />
        <Modal toggleModal={this.toggleModal} modalOpen={this.state.usernameModal} />
      </div>
    )
  }
}

const mapStateToProps = (state) => ({

})

import { initializeWs } from "./actions";
import { createGame } from '../GameBrowser/actions'
const mapDispatchToProps = {
  initializeWs,
  createGame
}

export default connect(mapStateToProps, mapDispatchToProps)(App)
