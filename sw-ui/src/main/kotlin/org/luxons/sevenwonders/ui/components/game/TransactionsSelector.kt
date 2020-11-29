package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.*
import kotlinx.css.*
import kotlinx.html.DIV
import kotlinx.html.TBODY
import kotlinx.html.TD
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.resources.*
import org.luxons.sevenwonders.ui.components.gameBrowser.playerInfo
import org.luxons.sevenwonders.ui.redux.TransactionSelectorState
import react.*
import react.dom.*
import styled.*

fun RBuilder.transactionsSelectorDialog(
    state: TransactionSelectorState?,
    neighbours: Pair<PlayerDTO, PlayerDTO>,
    prepareMove: (PlayerMove) -> Unit,
    cancelTransactionSelection: () -> Unit,
) {
    bpDialog(
        isOpen = state != null,
        title = "Trading time!",
        canEscapeKeyClose = true,
        canOutsideClickClose = true,
        isCloseButtonShown = true,
        onClose = cancelTransactionSelection,
    ) {
        attrs {
            className = GameStyles.getClassName { it::transactionsSelector }
        }
        div {
            attrs {
                classes += Classes.DIALOG_BODY
            }
            p {
                +"You don't have enough resources to perform this move, but you can buy them from neighbours. "
                +"Please pick an option:"
            }
            if (state != null) { // should always be true when the dialog is rendered
                styledDiv {
                    css {
                        margin(all = LinearDimension.auto)
                        display = Display.flex
                        alignItems = Align.center
                    }
                    neighbour(neighbours.first)
                    styledDiv {
                        css {
                            grow(Grow.GROW)
                            margin(horizontal = 0.5.rem)
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            alignItems = Align.center
                        }
                        optionsTable(state, prepareMove)
                    }
                    neighbour(neighbours.second)
                }
            }
        }
    }
}

private fun StyledDOMBuilder<DIV>.neighbour(player: PlayerDTO) {
    styledDiv {
        css {
            width = 100.pct

            // center the icon
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = Align.center
        }
        playerInfo(player, iconSize = 40, orientation = FlexDirection.column)
    }
}

private fun RBuilder.optionsTable(
    state: TransactionSelectorState,
    prepareMove: (PlayerMove) -> Unit,
) {
    child(optionsTable) {
        attrs {
            this.state = state
            this.prepareMove = prepareMove
        }
    }
}

private interface OptionsTableProps : RProps {
    var state: TransactionSelectorState
    var prepareMove: (PlayerMove) -> Unit
}

private val optionsTable = functionalComponent<OptionsTableProps> { props ->
    val state = props.state
    val prepareMove = props.prepareMove

    var expanded by useState { false }

    val bestPrice = state.transactionsOptions.bestPrice
    val (cheapestOptions, otherOptions) = state.transactionsOptions.partition { it.totalPrice == bestPrice }

    bpHtmlTable(interactive = true) {
        tbody {
            cheapestOptions.forEach { transactions ->
                transactionsOptionRow(
                    transactions = transactions,
                    showBestPriceIndicator = expanded,
                    onClick = { prepareMove(PlayerMove(state.moveType, state.card.name, transactions)) },
                )
            }
            if (expanded) {
                otherOptions.forEach { transactions ->
                    transactionsOptionRow(
                        transactions = transactions,
                        showBestPriceIndicator = false,
                        onClick = { prepareMove(PlayerMove(state.moveType, state.card.name, transactions)) },
                    )
                }
            }
        }
    }
    if (otherOptions.isNotEmpty()) {
        val icon = if (expanded) "chevron-up" else "chevron-down"
        val text = if (expanded) "Hide expensive options" else "Show more expensive options"
        bpButton(
            minimal = true,
            small = true,
            icon = icon,
            rightIcon = icon,
            onClick = { expanded = !expanded },
        ) {
            +text
        }
    }
}

private fun RDOMBuilder<TBODY>.transactionsOptionRow(
    transactions: PricedResourceTransactions,
    showBestPriceIndicator: Boolean,
    onClick: () -> Unit,
) {
    styledTr {
        css {
            cursor = Cursor.pointer
            alignItems = Align.center
        }
        attrs {
            onClickFunction = { onClick() }
        }
        // there should be at most one of each
        val leftTr = transactions.firstOrNull { it.provider == Provider.LEFT_PLAYER }
        val rightTr = transactions.firstOrNull { it.provider == Provider.RIGHT_PLAYER }
        styledTd {
            transactionCellCss()
            styledDiv {
                css { opacity = if (leftTr == null) 0.5 else 1 }
                transactionCellInnerCss()
                bpIcon(name = "caret-left", size = Icon.SIZE_LARGE)
                goldIndicator(leftTr?.totalPrice ?: 0, imgSize = 2.5.rem)
            }
        }
        styledTd {
            transactionCellCss()
            if (leftTr != null) {
                resourceList(leftTr.resources)
            }
        }
        styledTd {
            transactionCellCss()
            css { width = 1.5.rem }
            if (showBestPriceIndicator) {
                bestPriceIndicator()
            }
        }
        styledTd {
            transactionCellCss()
            if (rightTr != null) {
                resourceList(rightTr.resources)
            }
        }
        styledTd {
            transactionCellCss()
            styledDiv {
                css { opacity = if (rightTr == null) 0.5 else 1 }
                transactionCellInnerCss()
                goldIndicator(rightTr?.totalPrice ?: 0, imgSize = 2.5.rem)
                bpIcon(name = "caret-right", size = Icon.SIZE_LARGE)
            }
        }
    }
}

private fun StyledDOMBuilder<TD>.bestPriceIndicator() {
    styledDiv {
        css {
            +GameStyles.bestPrice
        }
        +"Best\nprice!"
    }
}

private fun StyledDOMBuilder<TD>.transactionCellCss() {
    // we need inline styles to win over BlueprintJS's styles (which are more specific than .class)
    inlineStyles {
        verticalAlign = VerticalAlign.middle
        textAlign = TextAlign.center
    }
}

private fun StyledDOMBuilder<DIV>.transactionCellInnerCss() {
    css {
        display = Display.flex
        flexDirection = FlexDirection.row
        alignItems = Align.center
    }
}

private fun RBuilder.resourceList(countedResources: List<CountedResource>) {
    val resources = countedResources.toRepeatedTypesList()

    // The biggest card is the Palace and requires 7 resources (1 of each).
    // We always have at least 1 resource on our wonder, so we'll never need to buy more than 6.
    // Therefore, 3 by row seems decent. When there are 4 items, it's visually better to have a 2x2 matrix, though.
    val rows = resources.chunked(if (resources.size == 4) 2 else 3)

    val imgSize = 1.5.rem
    styledDiv {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = Align.center
            justifyContent = JustifyContent.center
            grow(Grow.GROW)
            // this ensures stable dimensions, no matter how many resources (up to 2x3 matrix)
            width = imgSize * 3
            height = imgSize * 2
        }
        rows.forEach { row ->
            styledDiv {
                resourceRowCss()
                row.forEach {
                    resourceImage(it, size = imgSize)
                }
            }
        }
    }
}

private fun StyledDOMBuilder<DIV>.resourceRowCss() {
    css {
        display = Display.flex
        flexDirection = FlexDirection.row
        alignItems = Align.center
        margin(horizontal = LinearDimension.auto)
    }
}

private fun List<CountedResource>.toRepeatedTypesList(): List<ResourceType> = flatMap { cr -> List(cr.count) { cr.type } }
