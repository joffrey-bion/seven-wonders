package org.luxons.sevenwonders.ui.components.errors

import blueprintjs.core.*
import blueprintjs.icons.*
import kotlinx.browser.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.router.*
import react.*
import react.dom.html.ReactHTML.p
import react.redux.*
import redux.*

val ErrorDialog = FC {
    val dispatch = useDispatch<RAction, WrapperAction>()

    ErrorDialogPresenter {
        errorMessage = useSwSelector { it.fatalError }
        goHome = { dispatch(Navigate(SwRoute.HOME)) }
    }
}

private external interface ErrorDialogProps : Props {
    var errorMessage: String?
    var goHome: () -> Unit
}

private val ErrorDialogPresenter = FC<ErrorDialogProps>("ErrorDialogPresenter") { props ->
    val errorMessage = props.errorMessage
    BpDialog {
        isOpen = errorMessage != null
        titleText = "Oops!"
        icon = BpIcon.create {
            icon = IconNames.ERROR
            intent = Intent.DANGER
        }
        onClose = { goHomeAndRefresh() }

        BpDialogBody {
            p {
                +(errorMessage ?: "fatal error")
            }
        }
        BpDialogFooter {
            BpButton {
                icon = IconNames.LOG_OUT
                onClick = { goHomeAndRefresh() }

                +"HOME"
            }
        }
    }
}

private fun goHomeAndRefresh() {
    // we don't use a redux action here because we actually want to redirect and refresh the page
    window.location.href = SwRoute.HOME.path
}
