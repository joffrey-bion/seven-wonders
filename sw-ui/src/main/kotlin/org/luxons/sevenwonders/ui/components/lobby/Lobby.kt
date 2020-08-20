package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.Elevation
import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpButtonGroup
import com.palantir.blueprintjs.bpCard
import com.palantir.blueprintjs.bpDivider
import com.palantir.blueprintjs.bpNonIdealState
import kotlinx.css.*
import kotlinx.css.properties.*
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import org.luxons.sevenwonders.model.wonders.WonderSide
import org.luxons.sevenwonders.model.wonders.deal
import org.luxons.sevenwonders.model.wonders.withRandomSide
import org.luxons.sevenwonders.model.wonders.withSide
import org.luxons.sevenwonders.ui.components.GlobalStyles
import org.luxons.sevenwonders.ui.redux.RequestAddBot
import org.luxons.sevenwonders.ui.redux.RequestLeaveLobby
import org.luxons.sevenwonders.ui.redux.RequestReassignWonders
import org.luxons.sevenwonders.ui.redux.RequestReorderPlayers
import org.luxons.sevenwonders.ui.redux.RequestStartGame
import org.luxons.sevenwonders.ui.redux.connectStateAndDispatch
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledH2

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
                padding(1.rem)
                +GlobalStyles.fullscreen
            }
            h2 { +"${currentGame.name} — Lobby" }
            radialPlayerList(currentGame.players, currentPlayer)
            actionButtons(currentPlayer, currentGame)
            if (currentPlayer.isGameOwner) {
                setupPanel(currentGame)
            }
        }
    }

    private fun RBuilder.actionButtons(currentPlayer: PlayerDTO, currentGame: LobbyDTO) {
        styledDiv {
            css {
                position = Position.fixed
                bottom = 2.rem
                left = 50.pct
                transform { translate((-50).pct) }
            }
            if (currentPlayer.isGameOwner) {
                bpButtonGroup {
                    startButton(currentGame, currentPlayer)
                    addBotButton(currentGame)
                }
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

    private fun RBuilder.setupPanel(currentGame: LobbyDTO) {
        styledDiv {
            css {
                position = Position.fixed
                top = 2.rem
                right = 1.rem
                width = 15.rem
            }
            bpCard(Elevation.TWO) {
                styledH2 {
                    css {
                        margin(top = 0.px)
                    }
                    +"Game setup"
                }
                bpDivider()
                h3 {
                    +"Players"
                }
                reorderPlayersButton(currentGame)
                h3 {
                    +"Wonders"
                }
                randomizeWondersButton(currentGame)
                wonderSideSelectionGroup(currentGame)
            }
        }
    }

    private fun RBuilder.addBotButton(currentGame: LobbyDTO) {
        bpButton(
            large = true,
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
            icon = "random",
            rightIcon = "people",
            title = "Re-order players randomly",
            onClick = { reorderPlayers(currentGame) }
        ) {
            +"Reorder players"
        }
    }

    private fun reorderPlayers(currentGame: LobbyDTO) {
        props.reorderPlayers(currentGame.players.map { it.username }.shuffled())
    }

    private fun RBuilder.randomizeWondersButton(currentGame: LobbyDTO) {
        bpButton(
            icon = "random",
            title = "Re-assign wonders to players randomly",
            onClick = { randomizeWonders(currentGame) }
        ) {
            +"Randomize wonders"
        }
    }

    private fun RBuilder.wonderSideSelectionGroup(currentGame: LobbyDTO) {
        h4 {
            +"Select wonder sides:"
        }
        bpButtonGroup {
            bpButton(
                icon = "random",
                title = "Re-roll wonder sides randomly",
                onClick = { randomizeWonderSides(currentGame) }
            )
            bpButton(
                title = "Choose side A for everyone",
                onClick = { setWonderSides(currentGame, WonderSide.A) }
            ) {
                +"A"
            }
            bpButton(
                title = "Choose side B for everyone",
                onClick = { setWonderSides(currentGame, WonderSide.B) }
            ) {
                +"B"
            }
        }
    }

    private fun randomizeWonders(currentGame: LobbyDTO) {
        props.reassignWonders(currentGame.allWonders.deal(currentGame.players.size))
    }

    private fun randomizeWonderSides(currentGame: LobbyDTO) {
        props.reassignWonders(currentGame.players.map { currentGame.findWonder(it.wonder.name).withRandomSide() })
    }

    private fun setWonderSides(currentGame: LobbyDTO, side: WonderSide) {
        props.reassignWonders(currentGame.players.map { currentGame.findWonder(it.wonder.name).withSide(side) })
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
