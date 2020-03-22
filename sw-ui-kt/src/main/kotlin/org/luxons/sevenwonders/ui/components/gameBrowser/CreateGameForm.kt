package org.luxons.sevenwonders.ui.components.gameBrowser

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpInputGroup
import com.palantir.blueprintjs.org.luxons.sevenwonders.ui.components.gameBrowser.playerInfo
import com.palantir.blueprintjs.org.luxons.sevenwonders.ui.utils.createElement
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.JustifyContent
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.css.justifyContent
import kotlinx.html.js.onSubmitFunction
import org.luxons.sevenwonders.ui.redux.RequestCreateGameAction
import org.luxons.sevenwonders.ui.redux.connectDispatch
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RClass
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import styled.css
import styled.styledDiv

private interface CreateGameFormProps: RProps {
    var createGame: (String) -> Unit
}

private data class CreateGameFormState(var gameName: String = ""): RState

private class CreateGameForm(props: CreateGameFormProps): RComponent<CreateGameFormProps, CreateGameFormState>(props) {

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
                    placeholder = "Game name",
                    onChange = { e ->
                        val input = e.currentTarget as HTMLInputElement
                        setState(transformState = { CreateGameFormState(input.value) })
                    },
                    rightElement = createGameButton()
                )
            }
            playerInfo()
        }
    }

    private fun createGameButton() = createElement {
        bpButton(minimal = true, intent = Intent.PRIMARY, icon = "add", onClick = { e -> createGame(e) })
    }

    private fun createGame(e: Event) {
        e.preventDefault() // prevents refreshing the page when pressing Enter
        props.createGame(state.gameName)
    }
}

val createGameForm: RClass<RProps> = connectDispatch(CreateGameForm::class) { dispatch, _ ->
    createGame = { name -> dispatch(RequestCreateGameAction(name)) }
}
