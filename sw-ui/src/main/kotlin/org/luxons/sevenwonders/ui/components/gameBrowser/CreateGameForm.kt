package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.ui.redux.*
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form

val CreateGameForm = VFC {
    var gameName by useState("")

    val dispatch = useSwDispatch()
    val createGame = { dispatch(RequestCreateGame(gameName)) }

    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            justifyContent = JustifyContent.spaceBetween
        }
        form {
            onSubmit = { e ->
                e.preventDefault()
                createGame()
            }

            BpInputGroup {
                large = true
                placeholder = "Game name"
                onChange = { e ->
                    val input = e.currentTarget
                    gameName = input.value
                }
                rightElement = BpButton.create {
                    minimal = true
                    intent = Intent.PRIMARY
                    icon = IconNames.ADD
                    onClick = { e ->
                        e.preventDefault() // prevents refreshing the page when pressing Enter
                        createGame()
                    }
                }
            }
        }
    }
}
