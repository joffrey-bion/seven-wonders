# Seven Wonders Online

[![Travis Build](https://img.shields.io/travis/joffrey-bion/seven-wonders/master.svg)](https://travis-ci.org/joffrey-bion/seven-wonders)
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/joffrey-bion/seven-wonders/blob/master/LICENSE)

A digital version of the [7 Wonders board game](https://en.wikipedia.org/wiki/7_Wonders_(board_game)).

> :warning: **DISCLAIMER:** We do not own the rights on the 7 Wonders game concept and rules, 
> nor on the assets used here. This is a pet project, not intended to be sold.

## :construction: Work in progress

A staging version of the app is running [on heroku](https://seven-wonders-online.herokuapp.com/).
It is of course still under development, so some features are missing (some wonder bonuses for instance).

Also, the staging server is quite unstable as the CI/CD pipeline deploys the new app on it after every successful
build of the master branch. Don't play serious games there :smile:
 
### Server state

The websocket server API handles all the game steps, including special bonuses and end-of-game moves.

### Client state

The client handles the major features to play a full game:
- Username choice
- Create / Join a game
- View joined lobby / Add bot players
- Start game
- View personal board, and other players' board summaries
- Play/discard cards or upgrade wonder (auto-buying missing resources from neighbours)
- Display the score board

It lacks the following features:
- View full boards of other players
- Control which resources to buy from which neighbour
- "Special power" actions:
  - Play a card from discarded cards deck
  - Pick neighbour guild to copy
