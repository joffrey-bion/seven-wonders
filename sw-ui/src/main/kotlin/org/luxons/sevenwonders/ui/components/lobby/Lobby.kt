package org.luxons.sevenwonders.ui.components.lobby

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import csstype.Position
import emotion.react.*
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.wonders.*
import org.luxons.sevenwonders.ui.components.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.h4

private val BOT_NAMES = listOf("Wall-E", "B-Max", "Sonny", "T-800", "HAL", "GLaDOS", "R2-D2", "Bender", "AWESOM-O")

val Lobby = VFC(displayName = "Lobby") {
    val lobby = useSwSelector { it.currentLobby }
    val player = useSwSelector { it.currentPlayer }

    val dispatch = useSwDispatch()

    if (lobby == null || player == null) {
        BpNonIdealState {
            icon = IconNames.ERROR
            titleText = "Error: no current game"
        }
    } else {
        LobbyPresenter {
            currentGame = lobby
            currentPlayer = player

            startGame = { dispatch(RequestStartGame()) }
            addBot = { name -> dispatch(RequestAddBot(name)) }
            leaveLobby = { dispatch(RequestLeaveLobby()) }
            disbandLobby = { dispatch(RequestDisbandLobby()) }
            reorderPlayers = { orderedPlayers -> dispatch(RequestReorderPlayers(orderedPlayers)) }
            reassignWonders = { wonders -> dispatch(RequestReassignWonders(wonders)) }
        }
    }
}

private external interface LobbyPresenterProps : Props {
    var currentGame: LobbyDTO
    var currentPlayer: PlayerDTO
    var startGame: () -> Unit
    var addBot: (displayName: String) -> Unit
    var leaveLobby: () -> Unit
    var disbandLobby: () -> Unit
    var reorderPlayers: (orderedPlayers: List<String>) -> Unit
    var reassignWonders: (wonders: List<AssignedWonder>) -> Unit
}

private val LobbyPresenter = FC<LobbyPresenterProps> { props ->
    div {
        css(GlobalStyles.fullscreen, GlobalStyles.zeusBackground) {
            padding = Padding(all = 1.rem)
        }
        div {
            css(ClassName(Classes.DARK), LobbyStyles.contentContainer) {
                margin = Margin(vertical = 0.rem, horizontal = Auto.auto)
                maxWidth = GlobalStyles.preGameWidth
            }
            h1 { +"${props.currentGame.name} — Lobby" }

            radialPlayerList(props.currentGame.players, props.currentPlayer) {
                css {
                    // to make players more readable on the background
                    background = "radial-gradient(closest-side, black 20%, transparent)".unsafeCast<Gradient>()
                    // make it bigger so the background covers more ground
                    width = 40.rem
                    height = 40.rem
                }
            }
            actionButtons(props.currentPlayer, props.currentGame, props.startGame, props.leaveLobby, props.disbandLobby, props.addBot)

            if (props.currentPlayer.isGameOwner) {
                setupPanel(props.currentGame, props.reorderPlayers, props.reassignWonders)
            }
        }
    }
}

private fun ChildrenBuilder.actionButtons(
    currentPlayer: PlayerDTO,
    currentGame: LobbyDTO,
    startGame: () -> Unit,
    leaveLobby: () -> Unit,
    disbandLobby: () -> Unit,
    addBot: (String) -> Unit,
) {
    div {
        css {
            position = Position.absolute
            bottom = 2.rem
            left = 50.pct
            transform = translate((-50).pct)

            width = 70.pct
            display = Display.flex
            justifyContent = JustifyContent.spaceAround
        }
        if (currentPlayer.isGameOwner) {
            BpButtonGroup {
                leaveButton(leaveLobby)
                disbandButton(disbandLobby)
            }
            BpButtonGroup {
                addBotButton(currentGame, addBot)
                startButton(currentGame.startability(currentPlayer.username), startGame)
            }
        } else {
            leaveButton(leaveLobby)
        }
    }
}

private fun ChildrenBuilder.startButton(startability: Actionability, startGame: () -> Unit) {
    BpButton {
        large = true
        intent = Intent.PRIMARY
        icon = IconNames.PLAY
        title = startability.tooltip
        disabled = !startability.canDo
        onClick = { startGame() }

        +"START"
    }
}

private fun ChildrenBuilder.setupPanel(
    currentGame: LobbyDTO,
    reorderPlayers: (usernames: List<String>) -> Unit,
    reassignWonders: (wonders: List<AssignedWonder>) -> Unit,
) {
    div {
        className = LobbyStyles.setupPanel

        BpCard {
            elevation = Elevation.TWO
            className = ClassName(Classes.DARK)

            h2 {
                css {
                    marginTop = 0.px
                }
                +"Game setup"
            }
            BpDivider()
            h3 {
                +"Players"
            }
            reorderPlayersButton(currentGame, reorderPlayers)
            h3 {
                +"Wonders"
            }
            WonderSettingsGroup {
                this.currentGame = currentGame
                this.reassignWonders = reassignWonders
            }
        }
    }
}

private fun ChildrenBuilder.addBotButton(currentGame: LobbyDTO, addBot: (String) -> Unit) {
    BpButton {
        large = true
        icon = IconNames.PLUS
        rightIcon = IconNames.DESKTOP
        intent = Intent.PRIMARY
        title = if (currentGame.maxPlayersReached) "Max players reached" else "Add a bot to this game"
        disabled = currentGame.maxPlayersReached
        onClick = { addBot(randomBotNameUnusedIn(currentGame)) }
    }
}

private fun randomBotNameUnusedIn(currentGame: LobbyDTO): String {
    val availableBotNames = BOT_NAMES.filter { name ->
        currentGame.players.none { it.displayName == name }
    }
    return availableBotNames.random()
}

private fun ChildrenBuilder.reorderPlayersButton(currentGame: LobbyDTO, reorderPlayers: (usernames: List<String>) -> Unit) {
    BpButton {
        icon = IconNames.RANDOM
        rightIcon = IconNames.PEOPLE
        title = "Re-order players randomly"
        onClick = { reorderPlayers(currentGame.players.map { it.username }.shuffled()) }

        +"Reorder players"
    }
}

private external interface WonderSettingsGroupProps : Props {
    var currentGame: LobbyDTO
    var reassignWonders: (List<AssignedWonder>) -> Unit
}

private val WonderSettingsGroup = FC<WonderSettingsGroupProps> { props ->
    val reassignWonders = props.reassignWonders

    BpButton {
        icon = IconNames.RANDOM
        title = "Re-assign wonders to players randomly"
        onClick = { reassignWonders(randomWonderAssignments(props.currentGame)) }

        +"Randomize wonders"
    }
    h4 {
        +"Select wonder sides:"
    }
    BpButtonGroup {
        BpButton {
            icon = IconNames.RANDOM
            title = "Re-roll wonder sides randomly"
            onClick = { reassignWonders(assignedWondersWithRandomSides(props.currentGame)) }
        }
        BpButton {
            title = "Choose side A for everyone"
            onClick = { reassignWonders(assignedWondersWithForcedSide(props.currentGame, WonderSide.A)) }

            +"A"
        }
        BpButton {
            title = "Choose side B for everyone"
            onClick = { reassignWonders(assignedWondersWithForcedSide(props.currentGame, WonderSide.B)) }

            +"B"
        }
    }
}

private fun randomWonderAssignments(currentGame: LobbyDTO): List<AssignedWonder> =
    currentGame.allWonders.deal(currentGame.players.size)

private fun assignedWondersWithForcedSide(
    currentGame: LobbyDTO,
    side: WonderSide
) = currentGame.players.map { currentGame.findWonder(it.wonder.name).withSide(side) }

private fun assignedWondersWithRandomSides(currentGame: LobbyDTO) =
    currentGame.players.map { currentGame.findWonder(it.wonder.name) }.map { it.withRandomSide() }

private fun ChildrenBuilder.leaveButton(leaveLobby: () -> Unit) {
    BpButton {
        large = true
        intent = Intent.WARNING
        icon = "arrow-left"
        title = "Leave the lobby and go back to the game browser"
        onClick = { leaveLobby() }

        +"LEAVE"
    }
}

private fun ChildrenBuilder.disbandButton(disbandLobby: () -> Unit) {
    BpButton {
        large = true
        intent = Intent.DANGER
        icon = IconNames.DELETE
        title = "Disband the group and go back to the game browser"
        onClick = { disbandLobby() }

        +"DISBAND"
    }
}
