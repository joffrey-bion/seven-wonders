package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpNonIdealState
import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import org.luxons.sevenwonders.model.wonders.deal
import org.luxons.sevenwonders.ui.redux.*
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
    var reorderPlayers: (orderedPlayers: List<String>) -> Unit
    var reassignWonders: (wonders: List<AssignedWonder>) -> Unit
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
        styledDiv {
            css {
                margin(1.rem)
            }
            h2 { +"${currentGame.name} — Lobby" }
            radialPlayerList(currentGame.players, currentPlayer)
            actionButtons(currentPlayer, currentGame)
        }
    }

    private fun RDOMBuilder<DIV>.actionButtons(currentPlayer: PlayerDTO, currentGame: LobbyDTO) {
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
                reorderPlayersButton(currentGame)
                randomizeWondersButton(currentGame)
            } else {
                leaveButton()
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

    private fun RBuilder.reorderPlayersButton(currentGame: LobbyDTO) {
        bpButton(
            large = true,
            intent = Intent.NONE,
            icon = "random",
            rightIcon = "people",
            title = "Re-order players randomly",
            onClick = { reorderPlayers(currentGame) }
        )
    }

    private fun reorderPlayers(currentGame: LobbyDTO) {
        props.reorderPlayers(currentGame.players.map { it.username }.shuffled())
    }

    private fun RBuilder.randomizeWondersButton(currentGame: LobbyDTO) {
        bpButton(
            large = true,
            intent = Intent.NONE,
            icon = "random",
            title = "Re-assign wonders to players randomly",
            onClick = { randomizeWonders(currentGame) }
        ) {
            +"W"
        }
    }

    private fun randomizeWonders(currentGame: LobbyDTO) {
        props.reassignWonders(currentGame.allWonders.deal(currentGame.players.size))
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
        reorderPlayers = { orderedPlayers -> dispatch(RequestReorderPlayers(orderedPlayers)) }
        reassignWonders = { wonders -> dispatch(RequestReassignWonders(wonders)) }
    }
)
