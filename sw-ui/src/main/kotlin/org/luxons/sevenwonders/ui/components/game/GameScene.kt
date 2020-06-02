package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.*
import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.ui.components.GlobalStyles
import org.luxons.sevenwonders.ui.redux.*
import react.*
import styled.StyledDOMBuilder
import styled.css
import styled.getClassName
import styled.styledDiv

interface GameSceneStateProps : RProps {
    var playerIsReady: Boolean
    var players: List<PlayerDTO>
    var gameState: GameState?
    var preparedMove: PlayerMove?
    var preparedCard: HandCard?
}

interface GameSceneDispatchProps : RProps {
    var sayReady: () -> Unit
    var prepareMove: (move: PlayerMove) -> Unit
    var unprepareMove: () -> Unit
    var leaveGame: () -> Unit
}

interface GameSceneProps : GameSceneStateProps, GameSceneDispatchProps

private class GameScene(props: GameSceneProps) : RComponent<GameSceneProps, RState>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css {
                background = "url('images/backgrounds/papyrus.jpg')"
                backgroundSize = "cover"
                +GlobalStyles.fullscreen
            }
            val turnInfo = props.gameState?.turnInfo
            if (turnInfo == null) {
                bpNonIdealState(icon = "error", title = "Error: no turn info data")
            } else {
                turnInfoScene(turnInfo)
            }
        }
    }

    private fun RBuilder.turnInfoScene(turnInfo: PlayerTurnInfo) {
        val board = turnInfo.getOwnBoard()
        val leftBoard = turnInfo.getBoard(RelativeBoardPosition.LEFT)
        val rightBoard = turnInfo.getBoard(RelativeBoardPosition.RIGHT)
        val topBoards = (turnInfo.table.boards - board - leftBoard - rightBoard).reversed()
        styledDiv {
            css {
                height = 100.pct
            }
            turnInfo.scoreBoard?.let {
                scoreTableOverlay(it, props.players, props.leaveGame)
            }
            actionInfo(turnInfo.message)
            boardComponent(board = board) {
                css {
                    padding(all = 7.rem) // to fit the action info message & board summaries
                    width = 100.pct
                    height = 100.pct
                }
            }
            boardSummaries(leftBoard, rightBoard, topBoards)
            handRotationIndicator(turnInfo.table.handRotationDirection)
            handCards(turnInfo, props.preparedMove, props.prepareMove)
            val card = props.preparedCard
            val move = props.preparedMove
            if (card != null && move != null) {
                preparedMove(card, move)
            }
            if (turnInfo.action == Action.SAY_READY) {
                sayReadyButton()
            }
            productionBar(gold = board.gold, production = board.production)
        }
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
                bpCallout(intent = Intent.PRIMARY, icon = "info-sign") { +message }
            }
        }
    }

    private fun StyledDOMBuilder<DIV>.boardSummaries(leftBoard: Board, rightBoard: Board, topBoards: List<Board>) {
        // TODO use blueprint popover with full board preview
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

    private fun RBuilder.preparedMove(card: HandCard, move: PlayerMove) {
        bpOverlay(isOpen = true, onClose = props.unprepareMove) {
            preparedMove(card, move, props.unprepareMove) {
                css { +GlobalStyles.fixedCenter }
            }
        }
    }

    private fun RBuilder.sayReadyButton(): ReactElement {
        val isReady = props.playerIsReady
        val intent = if (isReady) Intent.SUCCESS else Intent.PRIMARY
        return styledDiv {
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
                    icon = if (isReady) "tick-circle" else "play",
                    onClick = { props.sayReady() }
                ) {
                    +"READY"
                }
                // not really a button, but nice for style
                bpButton(
                    large = true,
                    icon = "people",
                    disabled = isReady,
                    intent = intent
                ) {
                    +"${props.players.count { it.isReady }}/${props.players.size}"
                }
            }
        }
    }
}

fun RBuilder.gameScene() = gameScene {}

private val gameScene: RClass<GameSceneProps> =
        connectStateAndDispatch<GameSceneStateProps, GameSceneDispatchProps, GameSceneProps>(
            clazz = GameScene::class,
            mapDispatchToProps = { dispatch, _ ->
                prepareMove = { move -> dispatch(RequestPrepareMove(move)) }
                unprepareMove = { dispatch(RequestUnprepareMove()) }
                sayReady = { dispatch(RequestSayReady()) }
                leaveGame = { dispatch(RequestLeaveGame()) }
            },
            mapStateToProps = { state, _ ->
                playerIsReady = state.currentPlayer?.isReady == true
                players = state.gameState?.players ?: emptyList()
                gameState = state.gameState
                preparedMove = state.gameState?.currentPreparedMove
                preparedCard = state.gameState?.currentPreparedCard
            }
        )
