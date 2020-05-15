package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
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
            div { +"Error: no current game." }
            return
        }
        div {
            h2 { +"${currentGame.name} — Lobby" }
            radialPlayerList(currentGame.players, currentPlayer)
            if (currentPlayer.isGameOwner) {
                startButton(currentGame, currentPlayer)
                addBotButton()
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

    private fun RBuilder.addBotButton() {
        bpButton(
            large = true,
            intent = Intent.NONE,
            icon = "plus",
            rightIcon = "desktop",
            title = "Add a bot to this game",
            onClick = { addBot() }
        ) {
            +"ADD BOT"
        }
    }

    private fun addBot() {
        val name = listOf("Bob", "Jack", "John", "Boris", "HAL", "GLaDOS").random()
//        val botName = "\uD83E\uDD16 $name"
        props.addBot(name)
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
