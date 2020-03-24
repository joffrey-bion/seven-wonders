package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.ui.redux.RequestStartGameAction
import org.luxons.sevenwonders.ui.redux.connectStateAndDispatch
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

interface LobbyStateProps : RProps {
    var currentGame: LobbyDTO?
    var currentPlayer: PlayerDTO?
}

interface LobbyDispatchProps : RProps {
    var startGame: () -> Unit
}

interface LobbyProps : LobbyDispatchProps, LobbyStateProps

class LobbyPresenter(props: LobbyProps) : RComponent<LobbyProps, RState>(props) {

    override fun RBuilder.render() {
        val currentGame = props.currentGame
        val currentPlayer = props.currentPlayer
        if (currentGame == null || currentPlayer == null) {
            div { +"Error: no current game." }
            return
        }
        div {
            h2 { +"${currentGame.name} — Lobby" }
            radialPlayerList(currentGame.players)
            if (currentPlayer.isGameOwner) {
                bpButton(
                    large = true,
                    intent = Intent.PRIMARY,
                    icon = "play",
                    title = currentGame.startAction.tooltip,
                    disabled = !currentGame.startAction.canDo,
                    onClick = { props.startGame() }
                ) {
                    + "START"
                }
            }
        }
    }
}

fun RBuilder.lobby() = lobby {}

private val lobby = connectStateAndDispatch<LobbyStateProps, LobbyDispatchProps, LobbyProps>(
    clazz = LobbyPresenter::class,
    mapStateToProps = { state, _ ->
        currentGame = state.currentLobby
        currentPlayer = state.currentPlayer
    },
    mapDispatchToProps = { dispatch, _ ->
        startGame = { dispatch(RequestStartGameAction()) }
    }
)
