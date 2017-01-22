import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Heading, InlineForm } from 'rebass'
import { Link } from 'react-router'
class HomePage extends Component {

  play = (e) => {
    e.preventDefault()
    if (this._username !== undefined) {
      this.props.chooseUsername(this._username)
    }
  }

  render() {
    return (
      <div style={{maxWidth: '500px', margin: '0 auto'}}>
        <Heading>Enter your username to start playing!</Heading>
        <br />
        <InlineForm
          buttonLabel="Play now!"
          label="Username"
          name="username"
          onChange={(e) => this._username = e.target.value}
          onClick={this.play}
        />
        <Link to="/games">/games</Link>
        <br />
        <Link to="/somewhere">Take me somewhere</Link>
      </div>
    )
  }
}

const mapStateToProps = (state) => ({

})

import { actions } from '../redux/players'
const mapDispatchToProps = {
  chooseUsername: actions.chooseUsername
}

export default connect(mapStateToProps, mapDispatchToProps)(HomePage)
