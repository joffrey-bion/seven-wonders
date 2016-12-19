import React, { Component } from 'react'
import {
  Banner,
  Heading,
  Space,
  Button,
  Text
} from 'rebass'
import { Flex } from 'reflexbox'
import Modal from '../../components/modals/username'
import GameBrowser from '../GameBrowser'

class App extends Component {
  state = {
    usernameModal: false
  }

  componentDidMount() {

  }

  toggleModal = (key) => {
    return (e) => {
      const val = !this.state[key]
      this.setState({ [key]: val })
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
          <Button
            theme="success"
            children="Create Game"/>
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

// const mapStateToProps = (state) => ({
//
// })
//
// import { initializeWs } from "./actions";
// const mapDispatchToProps = {
//   initializeWs
// }

export default App
