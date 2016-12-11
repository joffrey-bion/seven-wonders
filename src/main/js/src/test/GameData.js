import React, { Component } from 'react';
import logo from '../logo.svg';
import '../App.css';

class GameData extends Component {
  constructor(props) {
    this.props = props;
  }
  render() {
    let cardItems = this.props.cards.map(c => <CardInfo key={c.name} card={c}/>);
    return (
      <ul className="App">
        {cardItems}
      </ul>
    );
  }
}

export default GameData;
