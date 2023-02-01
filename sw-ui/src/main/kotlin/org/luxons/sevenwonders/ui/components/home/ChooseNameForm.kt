package org.luxons.sevenwonders.ui.components.home

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.events.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import web.html.*

val ChooseNameForm = VFC {
    val dispatch = useSwDispatch()
    ChooseNameFormPresenter {
        chooseUsername = { name -> dispatch(RequestChooseName(name)) }
    }
}

private external interface ChooseNameFormPresenterProps : PropsWithChildren {
    var chooseUsername: (String) -> Unit
}

private val ChooseNameFormPresenter = FC<ChooseNameFormPresenterProps> { props ->
    var usernameState by useState("")

    form {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
        }
        onSubmit = { e ->
            e.preventDefault()
            props.chooseUsername(usernameState)
        }
        RandomNameButton {
            onClick = { usernameState = randomGreekName() }
        }
        spacer()
        BpInputGroup {
            large = true
            placeholder = "Username"
            rightElement = SubmitButton.create {
                onClick = { e ->
                    e.preventDefault()
                    props.chooseUsername(usernameState)
                }
            }
            value = usernameState
            onChange = { e ->
                val input = e.currentTarget
                usernameState = input.value
            }
        }
    }
}

private external interface SpecificButtonProps : Props {
    var onClick: MouseEventHandler<HTMLElement>?
}

private val SubmitButton = FC<SpecificButtonProps> { props ->
    BpButton {
        minimal = true
        icon = IconNames.ARROW_RIGHT
        intent = Intent.PRIMARY
        onClick = props.onClick
    }
}

private val RandomNameButton = FC<SpecificButtonProps> { props ->
    BpButton {
        title = "Generate random name"
        large = true
        icon = IconNames.RANDOM
        intent = Intent.PRIMARY
        onClick = props.onClick
    }
}

// TODO this is so bad I'm dying inside
private fun ChildrenBuilder.spacer() {
    div {
        css {
            margin = Margin(all = 2.px)
        }
    }
}
