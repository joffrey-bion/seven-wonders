package org.luxons.sevenwonders.ui.components.gameBrowser

import kotlinx.html.js.onClickFunction
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.ui.redux.RequestJoinGameAction
import org.luxons.sevenwonders.ui.redux.connect
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

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
            thead {
                gameListHeaderRow()
            }
            tbody {
                props.games.map {
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

private fun RBuilder.gameListItemRow(lobby: LobbyDTO, joinGame: (Long) -> Unit) = tr {
    th { +lobby.name }
    th { gameStatus(lobby.state) }
    th { playerCount(lobby.players.size) }
    th { joinButton(lobby, joinGame) }
}

private fun RBuilder.gameStatus(state: State) {
    span {
        +state.toString()
    }
}

private fun RBuilder.playerCount(nPlayers: Int) {
    span {
        +nPlayers.toString()
    }
}

private fun RBuilder.joinButton(lobby: LobbyDTO, joinGame: (Long) -> Unit) {
    button {
        attrs {
            disabled = lobby.state != State.LOBBY
            onClickFunction = { joinGame(lobby.id) }
        }
    }
}

val gameList = connect<GameListStateProps, GameListDispatchProps, GameListProps>(
    clazz = GameListPresenter::class,
    mapStateToProps = { state, _ ->
        games = state.games
    },
    mapDispatchToProps = { dispatch, _ ->
        joinGame = { gameId -> dispatch(RequestJoinGameAction(gameId = gameId)) }
    }
)
