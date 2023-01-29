package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.*
import blueprintjs.icons.IconNames
import csstype.*
import kotlinx.css.*
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.JustifyContent
import kotlinx.css.TextAlign
import kotlinx.css.VerticalAlign
import kotlinx.css.rem
import kotlinx.html.classes
import kotlinx.html.title
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.ui.redux.RequestJoinGame
import org.luxons.sevenwonders.ui.redux.connectStateAndDispatch
import react.*
import react.dom.*
import styled.*
import react.State as RState

external interface GameListStateProps : PropsWithChildren {
    var connectedPlayer: ConnectedPlayer
    var games: List<LobbyDTO>
}

external interface GameListDispatchProps : PropsWithChildren {
    var joinGame: (Long) -> Unit
}

external interface GameListProps : GameListStateProps, GameListDispatchProps

class GameListPresenter(props: GameListProps) : RComponent<GameListProps, RState>(props) {

    override fun RBuilder.render() {
        if (props.games.isEmpty()) {
            noGamesInfo()
        } else {
            gamesTable()
        }
    }

    private fun RBuilder.noGamesInfo() {
        bpNonIdealState(
            icon = IconNames.GEOSEARCH,
            title = "No games to join",
        ) {
            styledDiv {
                attrs {
                    classes += Classes.RUNNING_TEXT
                }
                css {
                    maxWidth = 35.rem
                }
                +"Nobody seems to be playing at the moment. "
                +"Don't be disappointed, you can always create your own game, and play with bots if you're alone."
            }
        }
    }

    private fun RBuilder.gamesTable() {
        bpHtmlTable {
            attrs {
                className = ClassName(GameBrowserStyles.getClassName { it::gameTable })
            }
            columnWidthsSpec()
            thead {
                gameListHeaderRow()
            }
            tbody {
                props.games.forEach {
                    gameListItemRow(it)
                }
            }
        }
    }

    private fun RElementBuilder<HTMLTableProps>.columnWidthsSpec() {
        colgroup {
            styledCol {
                css {
                    width = 40.rem
                }
            }
            styledCol {
                css {
                    width = 5.rem
                    textAlign = TextAlign.center
                }
            }
            styledCol {
                css {
                    width = 5.rem
                    textAlign = TextAlign.center // use inline style on th instead to overcome blueprint style
                }
            }
            styledCol {
                css {
                    width = 3.rem
                    textAlign = TextAlign.center
                }
            }
        }
    }

    private fun RBuilder.gameListHeaderRow() = tr {
        th {
            +"Name"
        }
        th {
            inlineStyles { gameTableHeaderCellStyle() }
            +"Status"
        }
        th {
            inlineStyles { gameTableHeaderCellStyle() }
            +"Players"
        }
        th {
            inlineStyles { gameTableHeaderCellStyle() }
            +"Join"
        }
    }

    private fun RBuilder.gameListItemRow(lobby: LobbyDTO) = styledTr {
        attrs {
            key = lobby.id.toString()
        }
        // inline styles necessary to overcome BlueprintJS's verticalAlign=top
        td {
            inlineStyles { gameTableCellStyle() }
            +lobby.name
        }
        td {
            inlineStyles {
                textAlign = TextAlign.center
                gameTableCellStyle()
            }
            gameStatus(lobby.state)
        }
        td {
            inlineStyles { gameTableCellStyle() }
            playerCount(lobby.players.size)
        }
        td {
            inlineStyles { gameTableCellStyle() }
            joinButton(lobby)
        }
    }

    private fun StyledElement.gameTableHeaderCellStyle() {
        textAlign = TextAlign.center
    }

    private fun StyledElement.gameTableCellStyle() {
        verticalAlign = VerticalAlign.middle
    }

    private fun RBuilder.gameStatus(state: State) {
        val intent = when (state) {
            State.LOBBY -> Intent.SUCCESS
            State.PLAYING -> Intent.WARNING
            State.FINISHED -> Intent.DANGER
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
                justifyContent = JustifyContent.center
            }
            attrs {
                title = "Number of players"
            }
            bpIcon(name = "people", title = null)
            styledSpan {
                css {
                    marginLeft = 0.3.rem
                }
                +nPlayers.toString()
            }
        }
    }

    private fun RBuilder.joinButton(lobby: LobbyDTO) {
        val joinability = lobby.joinability(props.connectedPlayer.displayName)
        bpButton(
            minimal = true,
            large = true,
            title = joinability.tooltip,
            icon = "arrow-right",
            disabled = !joinability.canDo,
            onClick = { props.joinGame(lobby.id) },
        )
    }
}

fun RBuilder.gameList() = gameList {}

private val gameList = connectStateAndDispatch<GameListStateProps, GameListDispatchProps, GameListProps>(
    clazz = GameListPresenter::class,
    mapStateToProps = { state, _ ->
        connectedPlayer = state.connectedPlayer ?: error("there should be a connected player")
        games = state.games
    },
    mapDispatchToProps = { dispatch, _ ->
        joinGame = { gameId -> dispatch(RequestJoinGame(gameId = gameId)) }
    },
)
