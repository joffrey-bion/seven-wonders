package org.luxons.sevenwonders.ui.components.home

import blueprintjs.core.*
import blueprintjs.icons.*
import emotion.react.*
import org.luxons.sevenwonders.ui.names.*
import org.luxons.sevenwonders.ui.redux.*
import react.*
import react.dom.html.ReactHTML.form
import web.cssom.*

val ChooseNameForm = FC {
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
        BpInputGroup {
            large = true
            placeholder = "Username"
            value = usernameState
            onChange = { e ->
                val input = e.currentTarget
                usernameState = input.value
            }
            rightElement = BpButton.create {
                title = "Generate random name"
                icon = IconNames.RANDOM
                minimal = true
                onClick = { usernameState = randomGreekName() }
            }
        }
        BpButton {
            title = "Start"
            icon = IconNames.ARROW_RIGHT
            intent = Intent.PRIMARY
            large = true
            onClick = { e ->
                e.preventDefault()
                props.chooseUsername(usernameState)
            }

            css {
                marginLeft = 0.2.rem
            }
        }
    }
}
