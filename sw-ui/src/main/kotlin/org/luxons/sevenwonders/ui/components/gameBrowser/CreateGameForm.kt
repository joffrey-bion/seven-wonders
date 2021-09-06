package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.Intent
import blueprintjs.core.bpButton
import blueprintjs.core.bpInputGroup
import blueprintjs.icons.IconNames
import kotlinx.css.*
import kotlinx.html.js.onSubmitFunction
import org.luxons.sevenwonders.ui.redux.RequestCreateGame
import org.luxons.sevenwonders.ui.redux.connectDispatch
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import styled.css
import styled.styledDiv

private interface CreateGameFormProps : PropsWithChildren {
    var createGame: (String) -> Unit
}

private data class CreateGameFormState(var gameName: String = "") : State

private class CreateGameForm(props: CreateGameFormProps) : RComponent<CreateGameFormProps, CreateGameFormState>(props) {

    override fun CreateGameFormState.init(props: CreateGameFormProps) {
        gameName = ""
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.spaceBetween
            }
            form {
                attrs {
                    onSubmitFunction = { e -> createGame(e) }
                }

                bpInputGroup(
                    large = true,
                    placeholder = "Game name",
                    onChange = { e ->
                        val input = e.currentTarget as HTMLInputElement
                        setState(transformState = { CreateGameFormState(input.value) })
                    },
                    rightElement = createGameButton(),
                )
            }
        }
    }

    private fun createGameButton() = buildElement {
        bpButton(minimal = true, intent = Intent.PRIMARY, icon = IconNames.ADD, onClick = { e -> createGame(e) })
    }

    private fun createGame(e: Event) {
        e.preventDefault() // prevents refreshing the page when pressing Enter
        props.createGame(state.gameName)
    }
}

val createGameForm: ComponentClass<PropsWithChildren> = connectDispatch(CreateGameForm::class) { dispatch, _ ->
    createGame = { name -> dispatch(RequestCreateGame(name)) }
}
