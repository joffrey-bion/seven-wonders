package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpButtonGroup
import com.palantir.blueprintjs.bpNonIdealState
import com.palantir.blueprintjs.org.luxons.sevenwonders.ui.components.game.handComponent
import kotlinx.css.CSSBuilder
import kotlinx.css.Overflow
import kotlinx.css.Position
import kotlinx.css.background
import kotlinx.css.backgroundSize
import kotlinx.css.bottom
import kotlinx.css.left
import kotlinx.css.overflow
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.css.right
import kotlinx.css.top
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.Action
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.ui.redux.RequestPrepareMove
import org.luxons.sevenwonders.ui.redux.RequestSayReady
import org.luxons.sevenwonders.ui.redux.connectStateAndDispatch
import org.luxons.sevenwonders.ui.utils.createElement
import react.RBuilder
import react.RClass
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.*
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv

interface GameSceneStateProps: RProps {
    var playerIsReady: Boolean
    var players: List<PlayerDTO>
    var turnInfo: PlayerTurnInfo?
}

interface GameSceneDispatchProps: RProps {
    var sayReady: () -> Unit
    var prepareMove: (move: PlayerMove) -> Unit
}

interface GameSceneProps : GameSceneStateProps, GameSceneDispatchProps

private class GameScene(props: GameSceneProps) : RComponent<GameSceneProps, RState>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css {
                background = "url('images/background-papyrus3.jpg')"
                backgroundSize = "cover"
                fullScreen()
            }
            val turnInfo = props.turnInfo
            if (turnInfo == null) {
                gamePreStart()
            } else {
                turnInfoScene(turnInfo)
            }
        }
    }

    private fun RBuilder.gamePreStart() {
        bpNonIdealState(
            description = createElement {
                p { +"Click 'ready' when you are"}
            },
            action = createElement {
                sayReadyButton()
            }
        )
    }

    private fun RBuilder.sayReadyButton(block: StyledDOMBuilder<DIV>.() -> Unit = {}): ReactElement {
        val isReady = props.playerIsReady
        val intent = if (isReady) Intent.SUCCESS else Intent.PRIMARY
        return styledDiv {
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
                bpButton(
                    large = true,
                    icon = "people",
                    disabled = isReady,
                    intent = intent
                ) {
                    +"${props.players.count { it.isReady }}/${props.players.size}"
                }
            }
            block()
        }
    }

    private fun RBuilder.turnInfoScene(turnInfo: PlayerTurnInfo) {
        val board = turnInfo.table.boards[turnInfo.playerIndex]
        div {
            // TODO use blueprint's Callout component without header and primary intent
            p { + turnInfo.message }
            boardComponent(board = board)
            val hand = turnInfo.hand
            if (hand != null) {
                handComponent(
                    cards = hand,
                    wonderUpgradable = turnInfo.wonderBuildability.isBuildable,
                    prepareMove = props.prepareMove
                )
            }
            if (turnInfo.action == Action.SAY_READY) {
                sayReadyButton {
                    css {
                        position = Position.absolute
                        bottom = 4.rem
                        left = 50.pct
                        transform { translate(tx = (-50).pct) }
                    }
                }
            }
            productionBar(gold = board.gold, production = board.production)
        }
    }
}

fun RBuilder.gameScene() = gameScene {}

private val gameScene: RClass<GameSceneProps> = connectStateAndDispatch<GameSceneStateProps, GameSceneDispatchProps,
        GameSceneProps>(
    clazz = GameScene::class,
    mapDispatchToProps = { dispatch, _ ->
        prepareMove = { move -> dispatch(RequestPrepareMove(move)) }
        sayReady = { dispatch(RequestSayReady()) }
    },
    mapStateToProps = { state, _ ->
        playerIsReady = state.currentPlayer?.isReady == true
        players = state.currentLobby?.players ?: emptyList()
        turnInfo = state.currentTurnInfo
    }
)

private fun CSSBuilder.fullScreen() {
    position = Position.fixed
    top = 0.px
    left = 0.px
    bottom = 0.px
    right = 0.px
    overflow = Overflow.hidden
}
