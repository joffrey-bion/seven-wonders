package org.luxons.sevenwonders.ui.components.gameBrowser

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import org.luxons.sevenwonders.ui.redux.RequestCreateGameAction
import org.luxons.sevenwonders.ui.redux.connectDispatch
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RClass
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

private interface CreateGameFormProps: RProps {
    var createGame: (String) -> Unit
}

private data class CreateGameFormState(var gameName: String = ""): RState

private class CreateGameForm(props: CreateGameFormProps): RComponent<CreateGameFormProps, CreateGameFormState>(props) {

    override fun CreateGameFormState.init(props: CreateGameFormProps) {
        gameName = ""
    }

    override fun RBuilder.render() {
        form {
            attrs.onSubmitFunction = { props.createGame(state.gameName) }

            input(type = InputType.text) {
                attrs {
                    value = state.gameName
                    onChangeFunction = { e ->
                        val input = e.currentTarget as HTMLInputElement
                        setState(transformState = { CreateGameFormState(input.value) })
                    }
                }
            }

            input(type = InputType.submit) {}
        }
    }
}

val createGameForm: RClass<RProps> = connectDispatch(CreateGameForm::class) { dispatch, _ ->
    createGame = { name -> dispatch(RequestCreateGameAction(name)) }
}
