package org.luxons.sevenwonders.ui.components.home

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpInputGroup
import com.palantir.blueprintjs.org.luxons.sevenwonders.ui.utils.createElement
import kotlinx.html.js.onSubmitFunction
import org.luxons.sevenwonders.ui.redux.RequestChooseName
import org.luxons.sevenwonders.ui.redux.connectDispatch
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RClass
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
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
            attrs.onSubmitFunction = { e -> chooseUsername(e) }
            bpInputGroup(
                large = true,
                placeholder = "Username",
                rightElement = submitButton(),
                onChange = { e ->
                    val input = e.currentTarget as HTMLInputElement
                    setState(transformState = { ChooseNameFormState(input.value) })
                }
            )
        }
    }

    private fun submitButton(): ReactElement = createElement {
        bpButton(
            minimal = true,
            icon = "arrow-right",
            intent = Intent.PRIMARY,
            onClick = { e -> chooseUsername(e) }
        )
    }

    private fun chooseUsername(e: Event) {
        e.preventDefault()
        props.chooseUsername(state.username)
    }
}

val chooseNameForm: RClass<RProps> = connectDispatch(ChooseNameForm::class) { dispatch, _ ->
    chooseUsername = { name -> dispatch(RequestChooseName(name)) }
}
