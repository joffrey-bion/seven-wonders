package org.luxons.sevenwonders.ui.components.game

import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.boards.*
import org.luxons.sevenwonders.model.cards.*
import org.luxons.sevenwonders.model.wonders.*
import react.*
import react.dom.html.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import web.cssom.*
import web.html.*

// card offsets in % of their size when displayed in columns
private const val xOffset = 20
private const val yOffset = 21

external interface BoardComponentProps : PropsWithClassName {
    var board: Board
}

val BoardComponent = FC<BoardComponentProps>("Board") { props ->
    div {
        className = props.className
        tableCards(cardColumns = props.board.playedCards)
        wonderComponent(wonder = props.board.wonder, military = props.board.military)
    }
}

private fun ChildrenBuilder.tableCards(cardColumns: List<List<TableCard>>) {
    div {
        css {
            display = Display.flex
            justifyContent = JustifyContent.spaceAround
            height = 45.pct
            width = 100.pct
        }
        cardColumns.forEach { cards ->
            TableCardColumn {
                this.key = cards.first().color.toString()
                this.cards = cards
            }
        }
    }
}

private external interface TableCardColumnProps : PropsWithClassName {
    var cards: List<TableCard>
}

private val TableCardColumn = FC<TableCardColumnProps>("TableCardColumn") { props ->
    div {
        css {
            height = 100.pct
            width = 13.pct
            marginRight = 4.pct
            position = Position.relative
        }
        props.cards.forEachIndexed { index, card ->
            TableCard {
                this.card = card
                this.indexInColumn = index
                this.key = card.name
            }
        }
    }
}

private external interface TableCardProps : PropsWithClassName {
    var card: TableCard
    var indexInColumn: Int
}

private val TableCard = FC<TableCardProps>("TableCard") { props ->
    val highlightColor = if (props.card.playedDuringLastMove) NamedColor.gold else null
    CardImage {
        this.card = props.card
        this.highlightColor = highlightColor

        css {
            position = Position.absolute
            zIndex = integer(props.indexInColumn + 2) // go above the board and the built wonder cards
            transform = translate(
                tx = (props.indexInColumn * xOffset).pct,
                ty = (props.indexInColumn * yOffset).pct,
            )
            maxWidth = 100.pct
            maxHeight = 70.pct

            hover {
                zIndex = integer(1000)
                maxWidth = 110.pct
                maxHeight = 75.pct
                hoverHighlightStyle()
            }
        }
    }
}

private fun ChildrenBuilder.wonderComponent(wonder: ApiWonder, military: Military) {
    div {
        css {
            position = Position.relative
            width = 100.pct
            height = 40.pct
        }
        div {
            css {
                position = Position.absolute
                left = 50.pct
                top = 0.px
                transform = translatex((-50).pct)
                height = 100.pct
                maxWidth = 95.pct // same as wonder

                // bring to the foreground on hover
                hover { zIndex = integer(50) }
            }
            img {
                src =  "/images/wonders/${wonder.image}"
                title = wonder.name
                alt = "Wonder ${wonder.name}"

                css {
                    borderRadius = "0.5%/1.5%".unsafeCast<BorderRadius>()
                    boxShadow = BoxShadow(color = NamedColor.black, offsetX = 0.2.rem, offsetY = 0.2.rem, blurRadius = 0.5.rem)
                    maxHeight = 100.pct
                    maxWidth = 100.pct
                    zIndex = integer(1) // go above the built wonder cards, but below the table cards

                    hover { hoverHighlightStyle() }
                }
            }
            div {
                css {
                    position = Position.absolute
                    top = 20.pct
                    right = (-80).px
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.start
                }
                victoryPoints(military.victoryPoints) {
                    css {
                        marginBottom = 5.px
                    }
                }
                defeatTokenCount(military.nbDefeatTokens) {
                    css {
                        marginTop = 5.px
                    }
                }
            }
            wonder.stages.forEachIndexed { index, stage ->
                WonderStageElement {
                    this.stage = stage
                    css {
                        wonderCardStyle(index, wonder.stages.size)
                    }
                }
            }
        }
    }
}

private fun ChildrenBuilder.victoryPoints(points: Int, block: HTMLAttributes<HTMLDivElement>.() -> Unit = {}) {
    boardToken("military/victory1", points, block)
}

private fun ChildrenBuilder.defeatTokenCount(nbDefeatTokens: Int, block: HTMLAttributes<HTMLDivElement>.() -> Unit = {}) {
    boardToken("military/defeat1", nbDefeatTokens, block)
}

private fun ChildrenBuilder.boardToken(tokenName: String, count: Int, block: HTMLAttributes<HTMLDivElement>.() -> Unit) {
    tokenWithCount(
        tokenName = tokenName,
        count = count,
        countPosition = TokenCountPosition.RIGHT,
        brightText = true,
    ) {
        css {
            filter = dropShadow(0.2.rem, 0.2.rem, 0.5.rem, NamedColor.black)
            height = 15.pct
        }
        block()
    }
}

private external interface WonderStageElementProps : PropsWithClassName {
    var stage: ApiWonderStage
}

private val WonderStageElement = FC<WonderStageElementProps>("WonderStageElement") { props ->
    val back = props.stage.cardBack
    if (back != null) {
        val highlightColor = if (props.stage.builtDuringLastMove) NamedColor.gold else null
        CardBackImage {
            this.cardBack = back
            this.highlightColor = highlightColor
            this.className = props.className
        }
    } else {
        CardPlaceholderImage {
            this.className = props.className
        }
    }
}

private fun PropertiesBuilder.wonderCardStyle(stageIndex: Int, nbStages: Int) {
    position = Position.absolute
    top = 60.pct // makes the cards stick out of the bottom of the wonder
    left = stagePositionPercent(stageIndex, nbStages).pct
    maxWidth = 24.pct // ratio of card width to wonder width
    maxHeight = 90.pct // ratio of card height to wonder height
    zIndex = integer(-1) // below wonder (somehow 0 is not sufficient)
}

private fun stagePositionPercent(stageIndex: Int, nbStages: Int): Double = when (nbStages) {
    2 -> 37.5 + stageIndex * 29.8 // 37.5 (29.8) 67.3
    4 -> -1.5 + stageIndex * 26.7 // -1.5 (26.6) 25.1 (26.8) 51.9 (26.7) 78.6
    else -> 7.9 + stageIndex * 30.0
}

private fun PropertiesBuilder.hoverHighlightStyle() {
    highlightStyle(NamedColor.palegoldenrod)
}
