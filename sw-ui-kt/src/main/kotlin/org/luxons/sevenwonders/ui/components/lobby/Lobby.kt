package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.ui.redux.RequestStartGameAction
import org.luxons.sevenwonders.ui.redux.connect
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

interface LobbyStateProps : RProps {
    var currentGame: LobbyDTO?
    var currentPlayer: PlayerDTO?
    var players: List<PlayerDTO>
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
            radialPlayerList(props.players)
            if (currentPlayer.isGameOwner) {
                bpButton(
                    large = true,
                    intent = Intent.PRIMARY,
                    icon = "play",
                    disabled = props.players.size < 3,
                    onClick = { props.startGame() }
                )
            }
        }
    }
}

fun RBuilder.lobby() = lobby {}

val lobby = connect<LobbyStateProps, LobbyDispatchProps, LobbyProps>(
    clazz = LobbyPresenter::class,
    mapStateToProps = { state, _ ->
        currentGame = state.lobby
        currentPlayer = state.player
        players = state.lobby?.players ?: emptyList()
    },
    mapDispatchToProps = { dispatch, _ ->
        startGame = { dispatch(RequestStartGameAction()) }
    }
)
