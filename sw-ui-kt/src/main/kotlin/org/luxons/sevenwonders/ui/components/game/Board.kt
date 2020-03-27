package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.Position
import kotlinx.css.TextAlign
import kotlinx.css.display
import kotlinx.css.height
import kotlinx.css.margin
import kotlinx.css.maxHeight
import kotlinx.css.maxWidth
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.boxShadow
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import kotlinx.css.rem
import kotlinx.css.textAlign
import kotlinx.css.vh
import kotlinx.css.vw
import kotlinx.css.width
import kotlinx.css.zIndex
import kotlinx.html.DIV
import kotlinx.html.title
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.wonders.ApiWonder
import react.RBuilder
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
            zIndex = indexInColumn
            transform {
                translate(
                    tx = (indexInColumn * xOffset).pct,
                    ty = (indexInColumn * yOffset).pct
                )
            }
        }
        block()
        cardImage(card = card) {
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
            width = 100.vw
            textAlign = TextAlign.center
        }
        styledImg(src="/images/wonders/${wonder.image}") {
            css {
                declarations["border-radius"] = "0.5%/1.5%"
                boxShadow(color = Color.black, offsetX = 0.2.rem, offsetY = 0.2.rem, blurRadius = 0.5.rem)
                maxHeight = 30.vh
                maxWidth = 95.vw
            }
            attrs {
                this.title = wonder.name
                this.alt = "Wonder ${wonder.name}"
            }
        }
    }
}
