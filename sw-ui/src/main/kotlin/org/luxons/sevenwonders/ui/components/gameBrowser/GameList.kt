package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.ReactHTML.col
import react.dom.html.ReactHTML.colgroup
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.thead
import react.dom.html.ReactHTML.tr
import react.State as RState

external interface GameListStateProps : Props {
    var connectedPlayer: ConnectedPlayer
    var games: List<LobbyDTO>
}

external interface GameListDispatchProps : Props {
    var joinGame: (Long) -> Unit
}

external interface GameListProps : GameListStateProps, GameListDispatchProps

val GameList = connectStateAndDispatch<GameListStateProps, GameListDispatchProps, GameListProps>(
    clazz = GameListPresenter::class,
    mapStateToProps = { state, _ ->
        connectedPlayer = state.connectedPlayer ?: error("there should be a connected player")
        games = state.games
    },
    mapDispatchToProps = { dispatch, _ ->
        joinGame = { gameId -> dispatch(RequestJoinGame(gameId = gameId)) }
    },
)

private class GameListPresenter(props: GameListProps) : Component<GameListProps, RState>(props) {

    override fun render() = Fragment.create {
        if (props.games.isEmpty()) {
            noGamesInfo()
        } else {
            gamesTable()
        }
    }

    private fun ChildrenBuilder.noGamesInfo() {
        BpNonIdealState {
            icon = IconNames.GEOSEARCH
            titleText = "No games to join"

            div {
                css(ClassName(Classes.RUNNING_TEXT)) {
                    maxWidth = 35.rem
                }
                +"Nobody seems to be playing at the moment. "
                +"Don't be disappointed, you can always create your own game, and play with bots if you're alone."
            }
        }
    }

    private fun ChildrenBuilder.gamesTable() {
        BpHTMLTable {
            css {
                width = 100.pct
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

    private fun ChildrenBuilder.columnWidthsSpec() {
        colgroup {
            col {
                css {
                    width = 40.rem
                }
            }
            col {
                css {
                    width = 5.rem
                    textAlign = TextAlign.center
                }
            }
            col {
                css {
                    width = 5.rem
                    textAlign = TextAlign.center // use inline style on th instead to overcome blueprint style
                }
            }
            col {
                css {
                    width = 3.rem
                    textAlign = TextAlign.center
                }
            }
        }
    }

    private fun ChildrenBuilder.gameListHeaderRow() = tr {
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

    private fun ChildrenBuilder.gameListItemRow(lobby: LobbyDTO) = tr {
        key = lobby.id.toString()
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

    private fun PropertiesBuilder.gameTableHeaderCellStyle() {
        textAlign = TextAlign.center
    }

    private fun PropertiesBuilder.gameTableCellStyle() {
        verticalAlign = VerticalAlign.middle
    }

    private fun ChildrenBuilder.gameStatus(state: State) {
        val intent = when (state) {
            State.LOBBY -> Intent.SUCCESS
            State.PLAYING -> Intent.WARNING
            State.FINISHED -> Intent.DANGER
        }
        BpTag {
            this.minimal = true
            this.intent = intent

            +state.toString()
        }
    }

    private fun ChildrenBuilder.playerCount(nPlayers: Int) {
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.center
            }
            title = "Number of players"
            BpIcon {
                icon = IconNames.PEOPLE
                title = null
            }
            span {
                css {
                    marginLeft = 0.3.rem
                }
                +nPlayers.toString()
            }
        }
    }

    private fun ChildrenBuilder.joinButton(lobby: LobbyDTO) {
        val joinability = lobby.joinability(props.connectedPlayer.displayName)
        BpButton {
            minimal = true
            large = true
            title = joinability.tooltip
            icon = "arrow-right"
            disabled = !joinability.canDo
            onClick = { props.joinGame(lobby.id) }
        }
    }
}
