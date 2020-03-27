package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpNonIdealState
import kotlinx.css.CSSBuilder
import kotlinx.css.Overflow
import kotlinx.css.Position
import kotlinx.css.background
import kotlinx.css.backgroundSize
import kotlinx.css.bottom
import kotlinx.css.left
import kotlinx.css.overflow
import kotlinx.css.position
import kotlinx.css.px
import kotlinx.css.right
import kotlinx.css.top
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
import react.dom.*
import styled.css
import styled.styledDiv

interface GameSceneStateProps: RProps {
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
                background = "url('images/background-papyrus.jpg')"
                backgroundSize = "cover"
                fullScreen()
            }
            val turnInfo = props.turnInfo
            if (turnInfo == null) {
                gamePreStart(props.sayReady)
            } else {
                turnInfoScene(turnInfo)
            }
        }
    }

    private fun RBuilder.gamePreStart(onReadyClicked: () -> Unit) {
        bpNonIdealState(
            description = createElement {
                p { +"Click 'ready' when you are"}
            },
            action = createElement {
                bpButton(
                    large = true,
                    intent = Intent.PRIMARY,
                    icon = "play",
                    onClick = { onReadyClicked() }
                ) {
                    +"READY"
                }
            }
        )
    }

    private fun RBuilder.turnInfoScene(turnInfo: PlayerTurnInfo) {
        val bd = turnInfo.table.boards[turnInfo.playerIndex];
        div {
            p { + turnInfo.message }
//            boardComponent(board = bd)
//            handComponent(
//                cards = turnInfo.hand,
//                wonderUpgradable = turnInfo.wonderBuildability.isBuildable,
//                prepareMove = props.prepareMove
//            )
            productionBar(
                gold = bd.gold,
                production = bd.production
            )
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
