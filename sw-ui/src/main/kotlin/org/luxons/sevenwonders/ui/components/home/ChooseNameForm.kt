package org.luxons.sevenwonders.ui.components.home

import blueprintjs.core.*
import blueprintjs.icons.*
import kotlinx.css.*
import kotlinx.html.js.*
import org.luxons.sevenwonders.ui.redux.*
import react.*
import styled.*

private external interface ChooseNameFormProps : PropsWithChildren {
    var chooseUsername: (String) -> Unit
}

private external interface ChooseNameFormState : State {
    var username: String
}

private class ChooseNameForm(props: ChooseNameFormProps) : RComponent<ChooseNameFormProps, ChooseNameFormState>(props) {

    override fun ChooseNameFormState.init(props: ChooseNameFormProps) {
        username = ""
    }

    override fun RBuilder.render() {
        styledForm {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
            }
            attrs.onSubmitFunction = { e ->
                e.preventDefault()
                chooseUsername()
            }
            randomNameButton()
            spacer()
            bpInputGroup(
                large = true,
                placeholder = "Username",
                rightElement = submitButton(),
                value = state.username,
                onChange = { e ->
                    val input = e.currentTarget
                    setState {
                        username = input.value
                    }
                },
            )
        }
    }

    private fun submitButton(): ReactElement<*> = buildElement {
        bpButton(
            minimal = true,
            icon = IconNames.ARROW_RIGHT,
            intent = Intent.PRIMARY,
            onClick = { e ->
                e.preventDefault()
                chooseUsername()
            },
        )
    }

    private fun RBuilder.randomNameButton() {
        bpButton(
            title = "Generate random name",
            large = true,
            icon = IconNames.RANDOM,
            intent = Intent.PRIMARY,
            onClick = { fillRandomUsername() },
        )
    }

    private fun fillRandomUsername() {
        setState { username = randomGreekName() }
    }

    private fun chooseUsername() {
        props.chooseUsername(state.username)
    }

    // TODO this is so bad I'm dying inside
    private fun RBuilder.spacer() {
        styledDiv {
            css {
                margin(2.px)
            }
        }
    }
}

val chooseNameForm: ComponentClass<PropsWithChildren> = connectDispatch(ChooseNameForm::class) { dispatch, _ ->
    chooseUsername = { name -> dispatch(RequestChooseName(name)) }
}
