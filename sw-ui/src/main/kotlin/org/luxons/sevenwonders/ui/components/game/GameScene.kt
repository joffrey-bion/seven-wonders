package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import csstype.Position
import emotion.react.*
import org.luxons.sevenwonders.client.*
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.boards.*
import org.luxons.sevenwonders.model.cards.*
import org.luxons.sevenwonders.model.resources.*
import org.luxons.sevenwonders.ui.components.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.ReactHTML.div

external interface GameSceneProps : Props {
    var currentPlayer: PlayerDTO?
    var players: List<PlayerDTO>
    var game: GameState
    var preparedMove: PlayerMove?
    var preparedCard: HandCard?
    var sayReady: () -> Unit
    var prepareMove: (move: PlayerMove) -> Unit
    var unprepareMove: () -> Unit
    var leaveGame: () -> Unit
}

data class TransactionSelectorState(
    val moveType: MoveType,
    val card: HandCard,
    val transactionsOptions: ResourceTransactionOptions,
)

val GameScene = VFC("GameScene") {

    val player = useSwSelector { it.currentPlayer }
    val gameState = useSwSelector { it.gameState }
    val dispatch = useSwDispatch()

    div {
        css(GlobalStyles.papyrusBackground, GlobalStyles.fullscreen) {}

        if (gameState == null) {
            BpNonIdealState {
                icon = IconNames.ERROR
                titleText = "Error: no game data"
            }
        } else {
            GameScenePresenter {
                currentPlayer = player
                players = gameState.players
                game = gameState
                preparedMove = gameState.currentPreparedMove
                preparedCard = gameState.currentPreparedCard

                prepareMove = { move -> dispatch(RequestPrepareMove(move)) }
                unprepareMove = { dispatch(RequestUnprepareMove()) }
                sayReady = { dispatch(RequestSayReady()) }
                leaveGame = { dispatch(RequestLeaveGame()) }
            }
        }
    }
}

private val GameScenePresenter = FC<GameSceneProps>("GameScenePresenter") { props ->
    var transactionSelectorState by useState<TransactionSelectorState>()

    val game = props.game
    val board = game.getOwnBoard()
    div {
        val maybeRed = GameStyles.pulsatingRed.takeIf { game.everyoneIsWaitingForMe() }
        css(maybeRed) {
            height = 100.pct
        }
        val action = game.action
        if (action is TurnAction.WatchScore) {
            scoreTableOverlay(action.scoreBoard, props.players, props.leaveGame)
        }
        actionInfo(game.action.message)
        BoardComponent {
            this.board = board
            css {
                padding = Padding(vertical = 7.rem, horizontal = 7.rem) // to fit the action info message & board summaries
                width = 100.pct
                height = 100.pct
            }
        }
        transactionsSelectorDialog(
            state = transactionSelectorState,
            neighbours = playerNeighbours(props.currentPlayer, props.players),
            prepareMove = { move ->
                props.prepareMove(move)
                transactionSelectorState = null
            },
            cancelTransactionSelection = { transactionSelectorState = null },
        )
        boardSummaries(game)
        handRotationIndicator(game.handRotationDirection)
        handCards(
            game = game,
            prepareMove = props.prepareMove,
            startTransactionsSelection = {
                transactionSelectorState = it
            }
        )
        val card = props.preparedCard
        val move = props.preparedMove
        if (card != null && move != null) {
            BpOverlay {
                isOpen = true
                onClose = { props.unprepareMove() }

                preparedMove(card, move, props.unprepareMove) {
                    css(GlobalStyles.fixedCenter) {}
                }
            }
        }
        if (game.action is TurnAction.SayReady) {
            SayReadyButton {
                currentPlayer = props.currentPlayer
                players = props.players
                sayReady = props.sayReady
            }
        }
    }
}

private fun GameState.everyoneIsWaitingForMe(): Boolean {
    val onlyMeInTheGame = players.count { it.isHuman } == 1
    if (onlyMeInTheGame || currentPreparedMove != null) {
        return false
    }
    return preparedCardsByUsername.values.count { it != null } == players.size - 1
}

private fun playerNeighbours(currentPlayer: PlayerDTO?, players: List<PlayerDTO>): Pair<PlayerDTO, PlayerDTO> {
    val me = currentPlayer?.username ?: error("we shouldn't be trying to display this if there is no player")
    val size = players.size
    val myIndex = players.indexOfFirst { it.username == me }
    return players[(myIndex - 1 + size) % size] to players[(myIndex + 1) % size]
}

private fun ChildrenBuilder.actionInfo(message: String) {
    div {
        css(ClassName(Classes.DARK)) {
            position = Position.fixed
            top = 0.pct
            left = 0.pct
            margin = Margin(vertical = 0.4.rem, horizontal = 0.4.rem)
            maxWidth = 25.pct // leave space for 4 board summaries when there are 7 players
        }
        BpCard {
            elevation = Elevation.TWO
            css {
                padding = Padding(all = 0.px)
            }

            BpCallout {
                intent = Intent.PRIMARY
                icon = IconNames.INFO_SIGN
                +message
            }
        }
    }
}

private fun ChildrenBuilder.boardSummaries(game: GameState) {
    val leftBoard = game.getBoard(RelativeBoardPosition.LEFT)
    val rightBoard = game.getBoard(RelativeBoardPosition.RIGHT)
    val topBoards = game.getNonNeighbourBoards().reversed()
    selfBoardSummary(game.getOwnBoard(), game.players)
    leftPlayerBoardSummary(leftBoard, game.players)
    rightPlayerBoardSummary(rightBoard, game.players)
    if (topBoards.isNotEmpty()) {
        topPlayerBoardsSummaries(topBoards, game.players)
    }
}

private fun ChildrenBuilder.leftPlayerBoardSummary(board: Board, players: List<PlayerDTO>) {
    div {
        css {
            position = Position.absolute
            left = 0.px
            bottom = 40.pct
        }
        BoardSummaryWithPopover {
            this.player = players[board.playerIndex]
            this.board = board
            this.side = BoardSummarySide.LEFT

            css {
                borderTopRightRadius = 0.4.rem
                borderBottomRightRadius = 0.4.rem
            }
        }
    }
}

private fun ChildrenBuilder.rightPlayerBoardSummary(board: Board, players: List<PlayerDTO>) {
    div {
        css {
            position = Position.absolute
            right = 0.px
            bottom = 40.pct
        }
        BoardSummaryWithPopover {
            this.player = players[board.playerIndex]
            this.board = board
            this.side = BoardSummarySide.RIGHT

            css {
                borderTopLeftRadius = 0.4.rem
                borderBottomLeftRadius = 0.4.rem
            }
        }
    }
}

private fun ChildrenBuilder.topPlayerBoardsSummaries(boards: List<Board>, players: List<PlayerDTO>) {
    div {
        css {
            position = Position.absolute
            top = 0.px
            left = 50.pct
            transform = translate((-50).pct)
            display = Display.flex
            flexDirection = FlexDirection.row
        }
        boards.forEach { board ->
            BoardSummaryWithPopover {
                this.player = players[board.playerIndex]
                this.board = board
                this.side = BoardSummarySide.TOP

                css {
                    borderBottomLeftRadius = 0.4.rem
                    borderBottomRightRadius = 0.4.rem
                    margin = Margin(vertical = 0.rem, horizontal = 2.rem)
                }
            }
        }
    }
}

private fun ChildrenBuilder.selfBoardSummary(board: Board, players: List<PlayerDTO>) {
    div {
        css {
            position = Position.absolute
            bottom = 0.px
            left = 0.px
        }
        BoardSummary {
            this.player = players[board.playerIndex]
            this.board = board
            this.side = BoardSummarySide.BOTTOM
            this.showPreparationStatus = false

            css {
                borderTopLeftRadius = 0.4.rem
                borderTopRightRadius = 0.4.rem
                margin = Margin(vertical = 0.rem, horizontal = 2.rem)
            }
        }
    }
}

private external interface SayReadyButtonProps : Props {
    var currentPlayer: PlayerDTO?
    var players: List<PlayerDTO>
    var sayReady: () -> Unit
}

private val SayReadyButton = FC<SayReadyButtonProps>("SayReadyButton") { props ->
    val isReady = props.currentPlayer?.isReady == true
    val intent = if (isReady) Intent.SUCCESS else Intent.PRIMARY
    div {
        css {
            position = Position.absolute
            bottom = 6.rem
            left = 50.pct
            transform = translate(tx = (-50).pct)
            zIndex = integer(2) // go above the wonder (1) and wonder-upgrade cards (0)
        }
        BpButtonGroup {
            BpButton {
                this.large = true
                this.disabled = isReady
                this.intent = intent
                this.icon = if (isReady) IconNames.TICK_CIRCLE else IconNames.PLAY
                this.onClick = { props.sayReady() }

                +"READY"
            }
            // not really a button, but nice for style
            BpButton {
                this.large = true
                this.icon = IconNames.PEOPLE
                this.disabled = isReady
                this.intent = intent

                +"${props.players.count { it.isReady }}/${props.players.size}"
            }
        }
    }
}
