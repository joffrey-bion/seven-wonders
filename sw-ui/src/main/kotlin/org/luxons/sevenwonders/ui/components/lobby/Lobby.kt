package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpNonIdealState
import kotlinx.css.*
import kotlinx.css.properties.*
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.ui.redux.RequestAddBot
import org.luxons.sevenwonders.ui.redux.RequestLeaveLobby
import org.luxons.sevenwonders.ui.redux.RequestStartGame
import org.luxons.sevenwonders.ui.redux.connectStateAndDispatch
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import styled.css
import styled.styledDiv

private val BOT_NAMES = listOf("Wall-E", "B-Max", "Sonny", "T-800", "HAL", "GLaDOS")

interface LobbyStateProps : RProps {
    var currentGame: LobbyDTO?
    var currentPlayer: PlayerDTO?
}

interface LobbyDispatchProps : RProps {
    var startGame: () -> Unit
    var addBot: (displayName: String) -> Unit
    var leaveLobby: () -> Unit
}

interface LobbyProps : LobbyDispatchProps, LobbyStateProps

class LobbyPresenter(props: LobbyProps) : RComponent<LobbyProps, RState>(props) {

    override fun RBuilder.render() {
        val currentGame = props.currentGame
        val currentPlayer = props.currentPlayer
        if (currentGame == null || currentPlayer == null) {
            bpNonIdealState(icon = "error", title = "Error: no current game")
            return
        }
        div {
            h2 { +"${currentGame.name} — Lobby" }
            radialPlayerList(currentGame.players, currentPlayer)

            styledDiv {
                css {
                    position = Position.fixed
                    bottom = 2.rem
                    left = 50.pct
                    transform { translate((-50).pct) }
                }
                if (currentPlayer.isGameOwner) {
                    startButton(currentGame, currentPlayer)
                    addBotButton(currentGame)
                } else {
                    leaveButton()
                }
            }
        }
    }

    private fun RBuilder.startButton(currentGame: LobbyDTO, currentPlayer: PlayerDTO) {
        val startability = currentGame.startability(currentPlayer.username)
        bpButton(
            large = true,
            intent = Intent.PRIMARY,
            icon = "play",
            title = startability.tooltip,
            disabled = !startability.canDo,
            onClick = { props.startGame() }
        ) {
            +"START"
        }
    }

    private fun RBuilder.addBotButton(currentGame: LobbyDTO) {
        bpButton(
            large = true,
            intent = Intent.NONE,
            icon = "plus",
            rightIcon = "desktop",
            title = if (currentGame.maxPlayersReached) "Max players reached" else "Add a bot to this game",
            disabled = currentGame.maxPlayersReached,
            onClick = { addBot(currentGame) }
        )
    }

    private fun addBot(currentGame: LobbyDTO) {
        val availableBotNames = BOT_NAMES.filter { name ->
            currentGame.players.all { it.displayName != name }
        }
        props.addBot(availableBotNames.random())
    }

    private fun RBuilder.leaveButton() {
        bpButton(
            large = true,
            intent = Intent.DANGER,
            icon = "arrow-left",
            title = "Leave the lobby and go back to the game browser",
            onClick = { props.leaveLobby() }
        ) {
            +"LEAVE"
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
        startGame = { dispatch(RequestStartGame()) }
        addBot = { name -> dispatch(RequestAddBot(name)) }
        leaveLobby = { dispatch(RequestLeaveLobby()) }
    }
)
