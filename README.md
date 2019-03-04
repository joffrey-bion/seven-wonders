# Seven Wonders Online

[![Travis Build](https://img.shields.io/travis/joffrey-bion/seven-wonders/master.svg)](https://travis-ci.org/joffrey-bion/seven-wonders)
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/joffrey-bion/seven-wonders/blob/master/LICENSE)

A digital version of the famous [7 Wonders board game](https://en.wikipedia.org/wiki/7_Wonders_(board_game)).

## Work in progress

A very early (staging) version of the app is running [on heroku](https://seven-wonders-online.herokuapp.com/).
It is of course still under development and not usable yet because the development of the client just started.
Also, it is very unstable as it is a staging server where the app is redeployed after every successful build of the 
master branch.
 
### Server

The websocket server API handles most of the game steps:
- Define your username
- Create / Join a game
- View joined lobby
- Start game
- View your hand and the table
- Make moves / See other's moves

Missing steps:
- End of game event
- Access scoring

A [live API documentation using JsonDoc](https://seven-wonders-online.herokuapp.com/livedoc-ui.html?url=https://seven-wonders-online.herokuapp.com/jsondoc)
is available. It is in construction as well because I'm adding wesocket support to the existing REST-API support. 

### Client

The client is just at the start of the development. It handles:
- Username choice
- Create / Join a game
- View joined lobby
- Start game
- View personal board
- Play/discard cards, upgrade wonder

Missing features:
- View other boards' elements
- Buy resources from neighbours
- Pick neighbour guild (for special power)
- Display winner and score

## Disclaimer

We do not own the rights on the 7 Wonders game concept and rules, nor on the assets used here.
