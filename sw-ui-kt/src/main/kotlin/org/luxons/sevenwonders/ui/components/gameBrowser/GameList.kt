package org.luxons.sevenwonders.ui.components.gameBrowser

import com.palantir.blueprintjs.Classes
import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpIcon
import com.palantir.blueprintjs.bpTag
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.VerticalAlign
import kotlinx.css.alignItems
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.css.verticalAlign
import kotlinx.html.classes
import kotlinx.html.title
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.ui.redux.RequestJoinGame
import org.luxons.sevenwonders.ui.redux.connectStateAndDispatch
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledSpan
import styled.styledTr

interface GameListStateProps : RProps {
    var games: List<LobbyDTO>
}

interface GameListDispatchProps: RProps {
    var joinGame: (Long) -> Unit
}

interface GameListProps : GameListStateProps, GameListDispatchProps

class GameListPresenter(props: GameListProps) : RComponent<GameListProps, RState>(props) {

    override fun RBuilder.render() {
        table {
            attrs {
                classes = setOf(Classes.HTML_TABLE)
            }
            thead {
                gameListHeaderRow()
            }
            tbody {
                props.games.forEach {
                    gameListItemRow(it, props.joinGame)
                }
            }
        }
    }
}

private fun RBuilder.gameListHeaderRow() = tr {
    th { +"Name" }
    th { +"Status" }
    th { +"Nb Players" }
    th { +"Join" }
}

private fun RBuilder.gameListItemRow(lobby: LobbyDTO, joinGame: (Long) -> Unit) = styledTr {
    css {
        verticalAlign = VerticalAlign.middle
    }
    attrs {
        key = lobby.id.toString()
    }
    td { +lobby.name }
    td { gameStatus(lobby.state) }
    td { playerCount(lobby.players.size) }
    td { joinButton(lobby, joinGame) }
}

private fun RBuilder.gameStatus(state: State) {
    val intent = when(state) {
        State.LOBBY -> Intent.SUCCESS
        State.PLAYING -> Intent.WARNING
    }
    bpTag(minimal = true, intent = intent) {
        +state.toString()
    }
}

private fun RBuilder.playerCount(nPlayers: Int) {
    styledDiv {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            alignItems = Align.center
        }
        attrs {
            title = "Number of players"
        }
        bpIcon(name = "people", title = null)
        styledSpan {
            +nPlayers.toString()
        }
    }
}

private fun RBuilder.joinButton(lobby: LobbyDTO, joinGame: (Long) -> Unit) {
    bpButton(
        minimal = true,
        title = lobby.joinAction.tooltip,
        icon = "arrow-right",
        disabled = !lobby.joinAction.canDo,
        onClick = { joinGame(lobby.id) }
    )
}

val gameList = connectStateAndDispatch<GameListStateProps, GameListDispatchProps, GameListProps>(
    clazz = GameListPresenter::class,
    mapStateToProps = { state, _ ->
        games = state.games
    },
    mapDispatchToProps = { dispatch, _ ->
        joinGame = { gameId -> dispatch(RequestJoinGame(gameId = gameId)) }
    }
)
