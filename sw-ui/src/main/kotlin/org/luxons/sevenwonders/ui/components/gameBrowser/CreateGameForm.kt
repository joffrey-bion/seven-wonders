package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.ui.names.*
import org.luxons.sevenwonders.ui.redux.*
import react.*
import react.dom.html.ReactHTML.form

val CreateGameForm = VFC {
    var gameName by useState("")

    val dispatch = useSwDispatch()
    val createGame = { dispatch(RequestCreateGame(gameName)) }

    form {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
        }
        onSubmit = { e ->
            e.preventDefault()
            createGame()
        }

        BpInputGroup {
            large = true
            placeholder = "Game name"
            value = gameName
            onChange = { e ->
                val input = e.currentTarget
                gameName = input.value
            }
            rightElement = BpButton.create {
                title = "Generate random name"
                icon = IconNames.RANDOM
                minimal = true
                onClick = { gameName = randomGameName() }
            }
        }
        BpButton {
            title = "Create the game"
            intent = Intent.PRIMARY
            icon = IconNames.ARROW_RIGHT
            large = true
            onClick = { e ->
                e.preventDefault() // prevents refreshing the page when pressing Enter
                createGame()
            }

            css {
                marginLeft = 0.2.rem
            }
        }
    }
}
