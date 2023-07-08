package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.score.*
import org.luxons.sevenwonders.ui.components.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.sup
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.thead
import react.dom.html.ReactHTML.tr
import web.cssom.*

external interface ScoreTableProps : Props {
    var scoreBoard: ScoreBoard
    var players: List<PlayerDTO>
}

external interface ScoreTableOverlayProps : ScoreTableProps {
    var isOpen: Boolean
    var leaveGame: () -> Unit
    var hideScoreBoard: () -> Unit
}

val ScoreTableOverlay = FC<ScoreTableOverlayProps>("ScoreTableOverlay") { props ->
    BpOverlay {
        isOpen = props.isOpen
        canOutsideClickClose = true
        canEscapeKeyClose = true
        onClose = { props.hideScoreBoard() }

        BpCard {
            css(GlobalStyles.fixedCenter, GameStyles.scoreBoard) {}

            div {
                // FIXME this doesn't look right, the scoreBoard class is applied at both levels
                css(GameStyles.scoreBoard) { // loads the styles so that they can be picked up by bpCard
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.center
                }
                h1 {
                    css {
                        marginTop = 0.px
                    }
                    +"Score Board"
                }
                ScoreTable {
                    scoreBoard = props.scoreBoard
                    players = props.players
                }
                div {
                    css {
                        marginTop = 1.rem
                        display = Display.flex
                        flexDirection = FlexDirection.row
                        alignItems = AlignItems.center
                    }
                    BpButton {
                        intent = Intent.PRIMARY
                        icon = IconNames.EYE_OPEN
                        large = true
                        onClick = { props.hideScoreBoard() }

                        css {
                            marginRight = 0.6.rem
                        }

                        +"VIEW TABLE"
                    }
                    BpButton {
                        intent = Intent.DANGER
                        icon = IconNames.LOG_OUT
                        large = true
                        onClick = { props.leaveGame() }

                        +"LEAVE"
                    }
                }
            }
        }
    }
}

private val ScoreTable = FC<ScoreTableProps>("ScoreTable") { props ->
    BpHTMLTable {
        bordered = false
        interactive = true

        thead {
            tr {
                th {
                    fullCenterInlineStyle()
                    +"Rank"
                }
                th {
                    fullCenterInlineStyle()
                    colSpan = 2

                    +"Player"
                }
                th {
                    fullCenterInlineStyle()
                    +"Score"
                }
                ScoreCategory.entries.forEach {
                    th {
                        fullCenterInlineStyle()
                        +it.title
                    }
                }
            }
        }
        tbody {
            props.scoreBoard.scores.forEachIndexed { index, score ->
                val player = props.players[score.playerIndex]
                tr {
                    td {
                        fullCenterInlineStyle()
                        ordinal(props.scoreBoard.ranks[index])
                    }
                    td {
                        fullCenterInlineStyle()
                        BpIcon {
                            icon = player.icon?.name ?: IconNames.USER
                            size = 25
                        }
                    }
                    td {
                        inlineStyles {
                            verticalAlign = VerticalAlign.middle
                        }
                        +player.displayName
                    }
                    td {
                        fullCenterInlineStyle()
                        BpTag {
                            large = true
                            round = true
                            minimal = true
                            className = GameStyles.totalScore

                            +"${score.totalPoints}"
                        }
                    }
                    ScoreCategory.entries.forEach { cat ->
                        td {
                            fullCenterInlineStyle()
                            BpTag {
                                large = true
                                round = true
                                fill = true
                                icon = cat.icon
                                className = classNameForCategory(cat)

                                +"${score.pointsByCategory[cat]}"
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun ChildrenBuilder.ordinal(value: Int) {
    +"$value"
    sup { +value.ordinalIndicator() }
}

private fun Int.ordinalIndicator() = when {
    this % 10 == 1 && this != 11 -> "st"
    this % 10 == 2 && this != 12 -> "nd"
    this % 10 == 3 && this != 13 -> "rd"
    else -> "th"
}

private fun HTMLAttributes<*>.fullCenterInlineStyle() {
    // inline styles necessary to overcome blueprintJS overrides
    inlineStyles {
        textAlign = TextAlign.center
        verticalAlign = VerticalAlign.middle
    }
}

private fun classNameForCategory(cat: ScoreCategory): ClassName = when (cat) {
    ScoreCategory.CIVIL -> GameStyles.civilScore
    ScoreCategory.SCIENCE -> GameStyles.scienceScore
    ScoreCategory.MILITARY -> GameStyles.militaryScore
    ScoreCategory.TRADE -> GameStyles.tradeScore
    ScoreCategory.GUILD -> GameStyles.guildScore
    ScoreCategory.WONDER -> GameStyles.wonderScore
    ScoreCategory.GOLD -> GameStyles.goldScore
}

private val ScoreCategory.icon: String
    get() = when (this) {
        ScoreCategory.CIVIL -> IconNames.OFFICE
        ScoreCategory.SCIENCE -> IconNames.LAB_TEST
        ScoreCategory.MILITARY -> IconNames.CUT
        ScoreCategory.TRADE -> IconNames.SWAP_HORIZONTAL
        ScoreCategory.GUILD -> IconNames.CLEAN // stars
        ScoreCategory.WONDER -> IconNames.SYMBOL_TRIANGLE_UP
        ScoreCategory.GOLD -> IconNames.DOLLAR
    }

// Potentially useful emojis:
// Greek temple:  🏛
// Cog (science): ⚙️
// Swords (war):  ⚔️
// Gold bag:      💰
