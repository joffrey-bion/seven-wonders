import React from 'react'
import {
  Banner
} from 'rebass'
import logo from './logo-7-wonders.png'
import background from './background-zeus-temple.jpg'

export default (props) => (
  <div>
    <Banner align="center" backgroundImage={background}>
      <img src={logo} alt="Seven Wonders"/>
      {props.children}
    </Banner>
  </div>
)
