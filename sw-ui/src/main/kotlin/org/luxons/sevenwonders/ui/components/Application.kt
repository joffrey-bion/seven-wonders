package org.luxons.sevenwonders.ui.components

import org.luxons.sevenwonders.ui.components.errors.*
import org.luxons.sevenwonders.ui.components.game.*
import org.luxons.sevenwonders.ui.components.gameBrowser.*
import org.luxons.sevenwonders.ui.components.home.*
import org.luxons.sevenwonders.ui.components.lobby.*
import org.luxons.sevenwonders.ui.router.*
import react.*
import react.router.*
import react.router.dom.*

val Application = VFC("Application") {
    HashRouter {
        ErrorDialog()
        Routes {
            Route {
                path = SwRoute.GAME_BROWSER.path
                element = GameBrowser.create()
            }
            Route {
                path = SwRoute.GAME.path
                element = GameScene.create()
            }
            Route {
                path = SwRoute.LOBBY.path
                element = Lobby.create()
            }
            Route {
                path = SwRoute.HOME.path
                element = Home.create()
            }
            Route {
                path = "*"
                element = Navigate.create {
                    to = "/"
                    replace = true
                }
            }
        }
    }
}

