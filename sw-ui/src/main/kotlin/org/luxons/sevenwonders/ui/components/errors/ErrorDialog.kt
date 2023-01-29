package org.luxons.sevenwonders.ui.components.errors

import blueprintjs.core.*
import blueprintjs.icons.*
import kotlinx.browser.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.router.*
import react.*
import react.dom.p
import styled.*

external interface ErrorDialogStateProps : PropsWithChildren {
    var errorMessage: String?
}

external interface ErrorDialogDispatchProps : PropsWithChildren {
    var goHome: () -> Unit
}

external interface ErrorDialogProps : ErrorDialogDispatchProps, ErrorDialogStateProps

class ErrorDialogPresenter(props: ErrorDialogProps) : RComponent<ErrorDialogProps, State>(props) {
    override fun RBuilder.render() {
        val errorMessage = props.errorMessage
        bpDialog(
            isOpen = errorMessage != null,
            title = "Oops!",
            icon = IconNames.ERROR,
            iconIntent = Intent.DANGER,
            onClose = { goHomeAndRefresh() }
        ) {
            styledDiv {
                css {
                    classes.add(Classes.DIALOG_BODY)
                }
                p {
                    +(errorMessage ?: "fatal error")
                }
            }
            styledDiv {
                css {
                    classes.add(Classes.DIALOG_FOOTER)
                }
                bpButton(icon = IconNames.LOG_OUT, onClick = { goHomeAndRefresh() }) {
                    +"HOME"
                }
            }
        }
    }
}

private fun goHomeAndRefresh() {
    // we don't use a redux action here because we actually want to redirect and refresh the page
    window.location.href = SwRoute.HOME.path
}

fun RBuilder.errorDialog() = errorDialog {}

private val errorDialog = connectStateAndDispatch<ErrorDialogStateProps, ErrorDialogDispatchProps, ErrorDialogProps>(
    clazz = ErrorDialogPresenter::class,
    mapStateToProps = { state, _ ->
        errorMessage = state.fatalError
    },
    mapDispatchToProps = { dispatch, _ ->
        goHome = { dispatch(Navigate(SwRoute.HOME)) }
    },
)
