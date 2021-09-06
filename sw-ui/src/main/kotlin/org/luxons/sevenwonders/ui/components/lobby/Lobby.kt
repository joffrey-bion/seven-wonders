package org.luxons.sevenwonders.ui.components.lobby

import blueprintjs.core.*
import blueprintjs.icons.IconNames
import kotlinx.css.*
import kotlinx.css.Position
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.wonders.*
import org.luxons.sevenwonders.ui.components.GlobalStyles
import org.luxons.sevenwonders.ui.redux.*
import react.*
import react.dom.h1
import react.dom.h3
import react.dom.h4
import styled.*

private val BOT_NAMES = listOf("Wall-E", "B-Max", "Sonny", "T-800", "HAL", "GLaDOS", "R2-D2", "Bender", "AWESOM-O")

interface LobbyStateProps : PropsWithChildren {
    var currentGame: LobbyDTO?
    var currentPlayer: PlayerDTO?
}

interface LobbyDispatchProps : PropsWithChildren {
    var startGame: () -> Unit
    var addBot: (displayName: String) -> Unit
    var leaveLobby: () -> Unit
    var disbandLobby: () -> Unit
    var reorderPlayers: (orderedPlayers: List<String>) -> Unit
    var reassignWonders: (wonders: List<AssignedWonder>) -> Unit
}

interface LobbyProps : LobbyDispatchProps, LobbyStateProps

class LobbyPresenter(props: LobbyProps) : RComponent<LobbyProps, State>(props) {

    override fun RBuilder.render() {
        val currentGame = props.currentGame
        val currentPlayer = props.currentPlayer
        if (currentGame == null || currentPlayer == null) {
            bpNonIdealState(icon = IconNames.ERROR, title = "Error: no current game")
            return
        }
        styledDiv {
            css {
                +GlobalStyles.fullscreen
                +GlobalStyles.zeusBackground
                padding(all = 1.rem)
            }
            styledDiv {
                css {
                    classes.add(Classes.DARK)
                    +LobbyStyles.contentContainer
                }
                h1 { +"${currentGame.name} — Lobby" }

                radialPlayerList(currentGame.players, currentPlayer) {
                    css {
                        // to make players more readable on the background
                        background = "radial-gradient(closest-side, black 20%, transparent)"
                        // make it bigger so the background covers more ground
                        width = 40.rem
                        height = 40.rem
                    }
                }
                actionButtons(currentPlayer, currentGame)

                if (currentPlayer.isGameOwner) {
                    setupPanel(currentGame)
                }
            }
        }
    }

    private fun RBuilder.actionButtons(currentPlayer: PlayerDTO, currentGame: LobbyDTO) {
        styledDiv {
            css {
                position = Position.absolute
                bottom = 2.rem
                left = 50.pct
                transform { translate((-50).pct) }

                width = 70.pct
                display = Display.flex
                justifyContent = JustifyContent.spaceAround
            }
            if (currentPlayer.isGameOwner) {
                bpButtonGroup {
                    leaveButton()
                    disbandButton()
                }
                bpButtonGroup {
                    addBotButton(currentGame)
                    startButton(currentGame, currentPlayer)
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
            icon = IconNames.PLAY,
            title = startability.tooltip,
            disabled = !startability.canDo,
            onClick = { props.startGame() },
        ) {
            +"START"
        }
    }

    private fun RBuilder.setupPanel(currentGame: LobbyDTO) {
        styledDiv {
            css {
                +LobbyStyles.setupPanel
            }
            bpCard(Elevation.TWO, className = Classes.DARK) {
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
            icon = IconNames.PLUS,
            rightIcon = IconNames.DESKTOP,
            intent = Intent.PRIMARY,
            title = if (currentGame.maxPlayersReached) "Max players reached" else "Add a bot to this game",
            disabled = currentGame.maxPlayersReached,
            onClick = { addBot(currentGame) },
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
            icon = IconNames.RANDOM,
            rightIcon = IconNames.PEOPLE,
            title = "Re-order players randomly",
            onClick = { reorderPlayers(currentGame) },
        ) {
            +"Reorder players"
        }
    }

    private fun reorderPlayers(currentGame: LobbyDTO) {
        props.reorderPlayers(currentGame.players.map { it.username }.shuffled())
    }

    private fun RBuilder.randomizeWondersButton(currentGame: LobbyDTO) {
        bpButton(
            icon = IconNames.RANDOM,
            title = "Re-assign wonders to players randomly",
            onClick = { randomizeWonders(currentGame) },
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
                icon = IconNames.RANDOM,
                title = "Re-roll wonder sides randomly",
                onClick = { randomizeWonderSides(currentGame) },
            )
            bpButton(
                title = "Choose side A for everyone",
                onClick = { setWonderSides(currentGame, WonderSide.A) },
            ) {
                +"A"
            }
            bpButton(
                title = "Choose side B for everyone",
                onClick = { setWonderSides(currentGame, WonderSide.B) },
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
            intent = Intent.WARNING,
            icon = "arrow-left",
            title = "Leave the lobby and go back to the game browser",
            onClick = { props.leaveLobby() },
        ) {
            +"LEAVE"
        }
    }

    private fun RBuilder.disbandButton() {
        bpButton(
            large = true,
            intent = Intent.DANGER,
            icon = IconNames.DELETE,
            title = "Disband the group and go back to the game browser",
            onClick = { props.disbandLobby() },
        ) {
            +"DISBAND"
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
        disbandLobby = { dispatch(RequestDisbandLobby()) }
        reorderPlayers = { orderedPlayers -> dispatch(RequestReorderPlayers(orderedPlayers)) }
        reassignWonders = { wonders -> dispatch(RequestReassignWonders(wonders)) }
    },
)
