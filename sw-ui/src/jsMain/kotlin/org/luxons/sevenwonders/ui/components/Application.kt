package org.luxons.sevenwonders.ui.components

import js.objects.*
import org.luxons.sevenwonders.ui.components.errors.*
import org.luxons.sevenwonders.ui.components.game.*
import org.luxons.sevenwonders.ui.components.gameBrowser.*
import org.luxons.sevenwonders.ui.components.home.*
import org.luxons.sevenwonders.ui.components.lobby.*
import org.luxons.sevenwonders.ui.router.*
import react.*
import react.router.*
import react.router.dom.*
import react.router.dom.RouterProvider

val Application = FC("Application") {
    ErrorDialog()
    RouterProvider {
        router = hashRouter
    }
}

// Using plain jso objects instead of createRoutesFromElements
// because of a broken Route external interface (no properties)
// See https://github.com/JetBrains/kotlin-wrappers/issues/2024
private val hashRouter = createHashRouter(
    routes = arrayOf(
        jso {
            path = SwRoute.GAME_BROWSER.path
            Component = GameBrowser
        },
        jso {
            path = SwRoute.GAME.path
            Component = GameScene
        },
        jso {
            path = SwRoute.LOBBY.path
            Component = Lobby
        },
        jso {
            path = SwRoute.HOME.path
            Component = Home
        },
        jso {
            path = "*"
            Component = FC {
                Navigate {
                    to = "/"
                    replace = true
                }
            }
        },
    ),
)
