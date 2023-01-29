package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.*
import blueprintjs.icons.IconNames
import kotlinx.css.*
import kotlinx.css.Position
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import org.luxons.sevenwonders.client.GameState
import org.luxons.sevenwonders.client.getBoard
import org.luxons.sevenwonders.client.getNonNeighbourBoards
import org.luxons.sevenwonders.client.getOwnBoard
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.TurnAction
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.resources.ResourceTransactionOptions
import org.luxons.sevenwonders.ui.components.GlobalStyles
import org.luxons.sevenwonders.ui.redux.*
import react.*
import styled.css
import styled.getClassName
import styled.styledDiv

external interface GameSceneStateProps : PropsWithChildren {
    var currentPlayer: PlayerDTO?
    var players: List<PlayerDTO>
    var game: GameState?
    var preparedMove: PlayerMove?
    var preparedCard: HandCard?
}

external interface GameSceneDispatchProps : PropsWithChildren {
    var sayReady: () -> Unit
    var prepareMove: (move: PlayerMove) -> Unit
    var unprepareMove: () -> Unit
    var leaveGame: () -> Unit
}

interface GameSceneProps : GameSceneStateProps, GameSceneDispatchProps

data class GameSceneState(
    var transactionSelector: TransactionSelectorState?
) : State

data class TransactionSelectorState(
    val moveType: MoveType,
    val card: HandCard,
    val transactionsOptions: ResourceTransactionOptions,
)

private class GameScene(props: GameSceneProps) : RComponent<GameSceneProps, GameSceneState>(props) {

    override fun GameSceneState.init() {
        transactionSelector = null
    }

    override fun RBuilder.render() {
        styledDiv {
            css {
                +GlobalStyles.papyrusBackground
                +GlobalStyles.fullscreen
            }
            val game = props.game
            if (game == null) {
                bpNonIdealState(icon = IconNames.ERROR, title = "Error: no game data")
            } else {
                boardScene(game)
            }
        }
    }

    private fun RBuilder.boardScene(game: GameState) {
        val board = game.getOwnBoard()
        styledDiv {
            css {
                height = 100.pct
                if (everyoneIsWaitingForMe()) {
                    +GameStyles.pulsatingRed
                }
            }
            val action = game.action
            if (action is TurnAction.WatchScore) {
                scoreTableOverlay(action.scoreBoard, props.players, props.leaveGame)
            }
            actionInfo(game.action.message)
            boardComponent(board = board) {
                css {
                    padding(all = 7.rem) // to fit the action info message & board summaries
                    width = 100.pct
                    height = 100.pct
                }
            }
            transactionsSelectorDialog(
                state = state.transactionSelector,
                neighbours = playerNeighbours(),
                prepareMove = ::prepareMoveAndCloseTransactions,
                cancelTransactionSelection = ::resetTransactionSelector,
            )
            boardSummaries(game)
            handRotationIndicator(game.handRotationDirection)
            handCards(game, props.prepareMove, ::startTransactionSelection)
            val card = props.preparedCard
            val move = props.preparedMove
            if (card != null && move != null) {
                preparedMove(card, move)
            }
            if (game.action is TurnAction.SayReady) {
                sayReadyButton()
            }
        }
    }

    private fun prepareMoveAndCloseTransactions(move: PlayerMove) {
        props.prepareMove(move)
        setState { transactionSelector = null }
    }

    private fun startTransactionSelection(selectorState: TransactionSelectorState) {
        setState { transactionSelector = selectorState }
    }

    private fun resetTransactionSelector() {
        setState { transactionSelector = null }
    }

    private fun everyoneIsWaitingForMe(): Boolean {
        val onlyMeInTheGame = props.players.count { it.isHuman } == 1
        if (onlyMeInTheGame || props.preparedMove != null) {
            return false
        }
        val gameState = props.game ?: return false
        return gameState.preparedCardsByUsername.values.count { it != null } == props.players.size - 1
    }

    private fun playerNeighbours(): Pair<PlayerDTO, PlayerDTO> {
        val me = props.currentPlayer?.username ?: error("we shouldn't be trying to display this if there is no player")
        val players = props.players
        val size = players.size
        val myIndex = players.indexOfFirst { it.username == me }
        return players[(myIndex - 1 + size) % size] to players[(myIndex + 1) % size]
    }

    private fun RBuilder.actionInfo(message: String) {
        styledDiv {
            css {
                classes.add("bp3-dark")
                position = Position.fixed
                top = 0.pct
                left = 0.pct
                margin(all = 0.4.rem)
                maxWidth = 25.pct // leave space for 4 board summaries when there are 7 players
            }
            bpCard(elevation = Elevation.TWO) {
                attrs {
                    this.className = GlobalStyles.getClassName { it::noPadding }
                }
                bpCallout(intent = Intent.PRIMARY, icon = IconNames.INFO_SIGN) { +message }
            }
        }
    }

    private fun RBuilder.boardSummaries(game: GameState) {
        val leftBoard = game.getBoard(RelativeBoardPosition.LEFT)
        val rightBoard = game.getBoard(RelativeBoardPosition.RIGHT)
        val topBoards = game.getNonNeighbourBoards().reversed()
        selfBoardSummary(game.getOwnBoard())
        leftPlayerBoardSummary(leftBoard)
        rightPlayerBoardSummary(rightBoard)
        if (topBoards.isNotEmpty()) {
            topPlayerBoardsSummaries(topBoards)
        }
    }

    private fun RBuilder.leftPlayerBoardSummary(board: Board) {
        styledDiv {
            css {
                position = Position.absolute
                left = 0.px
                bottom = 40.pct
            }
            boardSummaryWithPopover(props.players[board.playerIndex], board, BoardSummarySide.LEFT) {
                css {
                    borderTopRightRadius = 0.4.rem
                    borderBottomRightRadius = 0.4.rem
                }
            }
        }
    }

    private fun RBuilder.rightPlayerBoardSummary(board: Board) {
        styledDiv {
            css {
                position = Position.absolute
                right = 0.px
                bottom = 40.pct
            }
            boardSummaryWithPopover(props.players[board.playerIndex], board, BoardSummarySide.RIGHT) {
                css {
                    borderTopLeftRadius = 0.4.rem
                    borderBottomLeftRadius = 0.4.rem
                }
            }
        }
    }

    private fun RBuilder.topPlayerBoardsSummaries(boards: List<Board>) {
        styledDiv {
            css {
                position = Position.absolute
                top = 0.px
                left = 50.pct
                transform { translate((-50).pct) }
                display = Display.flex
                flexDirection = FlexDirection.row
            }
            boards.forEach { board ->
                boardSummaryWithPopover(props.players[board.playerIndex], board, BoardSummarySide.TOP) {
                    css {
                        borderBottomLeftRadius = 0.4.rem
                        borderBottomRightRadius = 0.4.rem
                        margin(horizontal = 2.rem)
                    }
                }
            }
        }
    }

    private fun RBuilder.selfBoardSummary(board: Board) {
        styledDiv {
            css {
                position = Position.absolute
                bottom = 0.px
                left = 0.px
            }
            boardSummary(
                player = props.players[board.playerIndex],
                board = board, BoardSummarySide.BOTTOM,
                showPreparationStatus = false,
            ) {
                css {
                    borderTopLeftRadius = 0.4.rem
                    borderTopRightRadius = 0.4.rem
                    margin(horizontal = 2.rem)
                }
            }
        }
    }

    private fun RBuilder.preparedMove(card: HandCard, move: PlayerMove) {
        bpOverlay(isOpen = true, onClose = props.unprepareMove) {
            preparedMove(card, move, props.unprepareMove) {
                css { +GlobalStyles.fixedCenter }
            }
        }
    }

    private fun RBuilder.sayReadyButton() {
        val isReady = props.currentPlayer?.isReady == true
        val intent = if (isReady) Intent.SUCCESS else Intent.PRIMARY
        styledDiv {
            css {
                position = Position.absolute
                bottom = 6.rem
                left = 50.pct
                transform { translate(tx = (-50).pct) }
                zIndex = 2 // go above the wonder (1) and wonder-upgrade cards (0)
            }
            bpButtonGroup {
                bpButton(
                    large = true,
                    disabled = isReady,
                    intent = intent,
                    icon = if (isReady) IconNames.TICK_CIRCLE else IconNames.PLAY,
                    onClick = { props.sayReady() },
                ) {
                    +"READY"
                }
                // not really a button, but nice for style
                bpButton(large = true, icon = IconNames.PEOPLE, disabled = isReady, intent = intent) {
                    +"${props.players.count { it.isReady }}/${props.players.size}"
                }
            }
        }
    }
}

fun RBuilder.gameScene() = gameScene {}

private val gameScene: ComponentClass<GameSceneProps> =
    connectStateAndDispatch<GameSceneStateProps, GameSceneDispatchProps, GameSceneProps>(
        clazz = GameScene::class,
        mapDispatchToProps = { dispatch, _ ->
            prepareMove = { move -> dispatch(RequestPrepareMove(move)) }
            unprepareMove = { dispatch(RequestUnprepareMove()) }
            sayReady = { dispatch(RequestSayReady()) }
            leaveGame = { dispatch(RequestLeaveGame()) }
        },
        mapStateToProps = { state, _ ->
            currentPlayer = state.currentPlayer
            players = state.gameState?.players ?: emptyList()
            game = state.gameState
            preparedMove = state.gameState?.currentPreparedMove
            preparedCard = state.gameState?.currentPreparedCard
        },
    )
