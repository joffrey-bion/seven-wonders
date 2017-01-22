import React from 'react'
import {
  Banner,
  Heading,
} from 'rebass'

export default (props) => (
  <div>
    <Banner
      align="center"
      style={{minHeight: '30vh', marginBottom: 0}}
      backgroundImage="https://images.unsplash.com/photo-1431207446535-a9296cf995b1?dpr=1&auto=format&fit=crop&w=1199&h=799&q=80&cs=tinysrgb&crop="
    >
      <Heading level={1}>Seven Wonders</Heading>
    </Banner>
    {props.children}
  </div>
)
