package org.luxons.sevenwonders.ui.components.home

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import org.luxons.sevenwonders.ui.redux.RequestChooseName
import org.luxons.sevenwonders.ui.redux.connectDispatch
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RClass
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

private interface ChooseNameFormProps: RProps {
    var chooseUsername: (String) -> Unit
}

private data class ChooseNameFormState(var username: String = ""): RState

private class ChooseNameForm(props: ChooseNameFormProps): RComponent<ChooseNameFormProps, ChooseNameFormState>(props) {

    override fun ChooseNameFormState.init(props: ChooseNameFormProps) {
        username = ""
    }

    override fun RBuilder.render() {
        form {
            attrs.onSubmitFunction = { props.chooseUsername(state.username) }

            input(type = InputType.text) {
                attrs {
                    value = state.username
                    onChangeFunction = { e ->
                        val input = e.currentTarget as HTMLInputElement
                        setState(transformState = { ChooseNameFormState(input.value) })
                    }
                }
            }
        }
    }
}

val chooseNameForm: RClass<RProps> = connectDispatch(ChooseNameForm::class) { dispatch, _ ->
    chooseUsername = { name -> dispatch(RequestChooseName(name)) }
}
