package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import kotlinx.html.DIV
import kotlinx.html.HTMLTag
import kotlinx.html.title
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.wonders.ApiWonder
import org.luxons.sevenwonders.model.wonders.ApiWonderStage
import react.RBuilder
import react.dom.*
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv
import styled.styledImg

// card offsets in % of their size when displayed in columns
private const val xOffset = 20
private const val yOffset = 21

fun RBuilder.boardComponent(board: Board) {
    styledDiv {
        css {
            width = 100.vw
        }
        tableCards(cardColumns = board.playedCards)
        wonderComponent(wonder = board.wonder)
    }
}

private fun RBuilder.tableCards(cardColumns: List<List<TableCard>>) {
    styledDiv {
        css {
            display = Display.flex
            height = 40.vh
            width = 100.vw
        }
        cardColumns.forEach { cards ->
            tableCardColumn(cards = cards) {
                attrs {
                    key = cards.first().color.toString()
                }
            }
        }
    }
}

private fun RBuilder.tableCardColumn(cards: List<TableCard>, block: StyledDOMBuilder<DIV>.() -> Unit = {}) {
    styledDiv {
        css {
            height = 40.vh
            width = 15.vw
            margin = "auto"
            position = Position.relative
        }
        block()
        cards.forEachIndexed { index, card ->
            tableCard(card = card, indexInColumn = index) {
                attrs { key = card.name }
            }
        }
    }
}

private fun RBuilder.tableCard(card: TableCard, indexInColumn: Int, block: StyledDOMBuilder<DIV>.() -> Unit = {}) {
    styledDiv {
        css {
            position = Position.absolute
            zIndex = indexInColumn + 2 // go above the board and the built wonder cards
            transform {
                translate(
                    tx = (indexInColumn * xOffset).pct,
                    ty = (indexInColumn * yOffset).pct
                )
            }
        }
        block()
        val highlightColor = if (card.playedDuringLastMove) Color.gold else null
        cardImage(card = card, highlightColor = highlightColor) {
            css {
                maxWidth = 10.vw
                maxHeight = 25.vh
            }
        }
    }
}

private fun RBuilder.wonderComponent(wonder: ApiWonder) {
    styledDiv {
        css {
            position = Position.relative
            width = 100.vw
        }
        styledImg(src = "/images/wonders/${wonder.image}") {
            css {
                position = Position.absolute
                left = 50.pct
                top = 0.px
                transform { translate((-50).pct) }
                declarations["border-radius"] = "0.5%/1.5%"
                boxShadow(color = Color.black, offsetX = 0.2.rem, offsetY = 0.2.rem, blurRadius = 0.5.rem)
                maxHeight = 30.vh
                maxWidth = 95.vw
                zIndex = 1 // go above the built wonder cards, but below the table cards
            }
            attrs {
                this.title = wonder.name
                this.alt = "Wonder ${wonder.name}"
            }
        }
        styledDiv {
            css {
                position = Position.absolute
                left = 50.pct
                top = 0.px
                transform { translate((-50).pct, (60).pct) }
                display = Display.flex
                justifyContent = JustifyContent.spaceAround
                flexWrap = FlexWrap.nowrap
                maxHeight = 30.vh // same as wonder
                maxWidth = 95.vw // same as wonder
                zIndex = 0 // below wonder
            }
            wonder.stages.forEach {
                wonderStageElement(it) {
                    css {
                        wonderCardStyle()
                    }
                }
            }
        }
    }
}

private fun StyledDOMBuilder<DIV>.wonderStageElement(it: ApiWonderStage, block: StyledDOMBuilder<HTMLTag>.() -> Unit) {
    val back = it.cardBack
    if (back != null) {
        cardBackImage(back) {
            block()
        }
    } else {
        cardPlaceholderImage {
            block()
        }
    }
}

fun CSSBuilder.wonderCardStyle() {
    maxWidth = 10.vw
    maxHeight = 25.vh
    margin(vertical = 0.px, horizontal = 6.3.pct)
}
