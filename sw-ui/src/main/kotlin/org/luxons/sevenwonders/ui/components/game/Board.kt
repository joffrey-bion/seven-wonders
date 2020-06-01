package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import kotlinx.html.HTMLTag
import kotlinx.html.IMG
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

fun RBuilder.boardComponent(board: Board, block: StyledDOMBuilder<DIV>.() -> Unit = {}) {
    styledDiv {
        block()
        tableCards(cardColumns = board.playedCards)
        wonderComponent(wonder = board.wonder)
    }
}

private fun RBuilder.tableCards(cardColumns: List<List<TableCard>>) {
    styledDiv {
        css {
            display = Display.flex
            justifyContent = JustifyContent.spaceAround
            height = 45.pct
            width = 100.pct
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
            height = 100.pct
            width = 13.pct
            marginRight = 4.pct
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

private fun RBuilder.tableCard(card: TableCard, indexInColumn: Int, block: StyledDOMBuilder<IMG>.() -> Unit = {}) {
    val highlightColor = if (card.playedDuringLastMove) Color.gold else null
    cardImage(card = card, highlightColor = highlightColor) {
        css {
            position = Position.absolute
            zIndex = indexInColumn + 2 // go above the board and the built wonder cards
            transform {
                translate(
                    tx = (indexInColumn * xOffset).pct,
                    ty = (indexInColumn * yOffset).pct
                )
            }
            maxWidth = 100.pct
            maxHeight = 70.pct
        }
        block()
    }
}

private fun RBuilder.wonderComponent(wonder: ApiWonder) {
    styledDiv {
        css {
            position = Position.relative
            width = 100.pct
            height = 40.pct
        }
        styledDiv {
            css {
                position = Position.absolute
                left = 50.pct
                top = 0.px
                transform { translateX((-50).pct) }
                height = 100.pct
                maxWidth = 95.pct // same as wonder
            }
            styledImg(src = "/images/wonders/${wonder.image}") {
                css {
                    declarations["border-radius"] = "0.5%/1.5%"
                    boxShadow(color = Color.black, offsetX = 0.2.rem, offsetY = 0.2.rem, blurRadius = 0.5.rem)
                    maxHeight = 100.pct
                    maxWidth = 100.pct
                    zIndex = 1 // go above the built wonder cards, but below the table cards
                }
                attrs {
                    this.title = wonder.name
                    this.alt = "Wonder ${wonder.name}"
                }
            }
            wonder.stages.forEachIndexed { index, stage ->
                wonderStageElement(stage) {
                    css {
                        wonderCardStyle()
                        left = stagePositionPercent(index, wonder.stages.size).pct
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
    position = Position.absolute
    top = 60.pct // makes the cards stick out of the bottom of the wonder
    maxWidth = 24.pct // ratio of card width to wonder width
    maxHeight = 90.pct // ratio of card height to wonder height
    zIndex = -1 // below wonder
}

private fun stagePositionPercent(stageIndex: Int, nbStages: Int): Double = when (nbStages) {
    2 -> 37.5 + stageIndex * 29.8 // 37.5 (29.8) 67.3
    4 -> -1.5 + stageIndex * 26.7 // -1.5 (26.6) 25.1 (26.8) 51.9 (26.7) 78.6
    else -> 7.9 + stageIndex * 30.0
}
