package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.resources.*
import org.luxons.sevenwonders.model.resources.Provider
import org.luxons.sevenwonders.ui.components.gameBrowser.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.tbody
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.tr
import web.html.*

fun ChildrenBuilder.transactionsSelectorDialog(
    state: TransactionSelectorState?,
    neighbours: Pair<PlayerDTO, PlayerDTO>,
    prepareMove: (PlayerMove) -> Unit,
    cancelTransactionSelection: () -> Unit,
) {
    BpDialog {
        isOpen = state != null
        titleText = "Trading time!"
        canEscapeKeyClose = true
        canOutsideClickClose = true
        isCloseButtonShown = true
        onClose = { cancelTransactionSelection() }

        className = GameStyles.transactionsSelector

        BpDialogBody {
            p {
                +"You don't have enough resources to perform this move, but you can buy them from neighbours. "
                +"Please pick an option:"
            }
            if (state != null) { // should always be true when the dialog is rendered
                div {
                    css {
                        margin = Margin(all = Auto.auto)
                        display = Display.flex
                        alignItems = AlignItems.center
                    }
                    neighbour(neighbours.first)
                    div {
                        css {
                            flexGrow = number(1.0)
                            margin = Margin(vertical = 0.rem, horizontal = 0.5.rem)
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            alignItems = AlignItems.center
                        }
                        OptionsTable {
                            this.state = state
                            this.prepareMove = prepareMove
                        }
                    }
                    neighbour(neighbours.second)
                }
            }
        }
    }
}

private fun ChildrenBuilder.neighbour(player: PlayerDTO) {
    div {
        css {
            width = 12.rem

            // center the icon
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }
        PlayerInfo {
            this.player = player
            this.iconSize = 40
            this.orientation = FlexDirection.column
            this.ellipsize = false
        }
    }
}

private external interface OptionsTableProps : PropsWithChildren {
    var state: TransactionSelectorState
    var prepareMove: (PlayerMove) -> Unit
}

private val OptionsTable = FC<OptionsTableProps> { props ->
    val state = props.state
    val prepareMove = props.prepareMove

    var expanded by useState { false }

    val bestPrice = state.transactionsOptions.bestPrice
    val (cheapestOptions, otherOptions) = state.transactionsOptions.partition { it.totalPrice == bestPrice }

    BpHTMLTable {
        interactive = true
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
        BpButton {
            this.minimal = true
            this.small = true
            this.icon = icon
            this.rightIcon = icon
            this.onClick = { expanded = !expanded }

            +text
        }
    }
}

private fun ChildrenBuilder.transactionsOptionRow(
    transactions: PricedResourceTransactions,
    showBestPriceIndicator: Boolean,
    onClick: () -> Unit,
) {
    tr {
        css {
            cursor = Cursor.pointer
            alignItems = AlignItems.center
        }
        this.onClick = { onClick() }
        // there should be at most one of each
        val leftTr = transactions.firstOrNull { it.provider == Provider.LEFT_PLAYER }
        val rightTr = transactions.firstOrNull { it.provider == Provider.RIGHT_PLAYER }
        td {
            transactionCellCss()
            div {
                css { opacity = number(if (leftTr == null) 0.5 else 1.0) }
                transactionCellInnerCss()
                BpIcon {
                    icon = IconNames.CARET_LEFT
                    size = IconSize.LARGE
                }
                goldIndicator(leftTr?.totalPrice ?: 0, imgSize = 2.5.rem)
            }
        }
        td {
            transactionCellCss()
            if (leftTr != null) {
                resourceList(leftTr.resources)
            }
        }
        td {
            transactionCellCss()
            css { width = 1.5.rem }
            if (showBestPriceIndicator) {
                bestPriceIndicator()
            }
        }
        td {
            transactionCellCss()
            if (rightTr != null) {
                resourceList(rightTr.resources)
            }
        }
        td {
            transactionCellCss()
            div {
                css { opacity = number(if (rightTr == null) 0.5 else 1.0) }
                transactionCellInnerCss()
                goldIndicator(rightTr?.totalPrice ?: 0, imgSize = 2.5.rem)
                BpIcon {
                    icon = IconNames.CARET_RIGHT
                    size = IconSize.LARGE
                }
            }
        }
    }
}

private fun ChildrenBuilder.bestPriceIndicator() {
    div {
        css(GameStyles.bestPrice){}
        +"Best\nprice!"
    }
}

private fun HTMLAttributes<HTMLTableCellElement>.transactionCellCss() {
    // we need inline styles to win over BlueprintJS's styles (which are more specific than .class)
    inlineStyles {
        verticalAlign = VerticalAlign.middle
        textAlign = TextAlign.center
    }
}

private fun HTMLAttributes<HTMLDivElement>.transactionCellInnerCss() {
    css {
        display = Display.flex
        flexDirection = FlexDirection.row
        alignItems = AlignItems.center
    }
}

private fun ChildrenBuilder.resourceList(countedResources: List<CountedResource>) {
    val resources = countedResources.toRepeatedTypesList()

    // The biggest card is the Palace and requires 7 resources (1 of each).
    // We always have at least 1 resource on our wonder, so we'll never need to buy more than 6.
    // Therefore, 3 by row seems decent. When there are 4 items, it's visually better to have a 2x2 matrix, though.
    val rows = resources.chunked(if (resources.size == 4) 2 else 3)

    val imgSize = 1.5
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            justifyContent = JustifyContent.center
            flexGrow = number(1.0)
            // this ensures stable dimensions, no matter how many resources (up to 2x3 matrix)
            width = (imgSize * 3).rem
            height = (imgSize * 2).rem
        }
        rows.forEach { row ->
            div {
                resourceRowCss()
                row.forEach {
                    resourceImage(it, size = imgSize.rem)
                }
            }
        }
    }
}

private fun HTMLAttributes<HTMLDivElement>.resourceRowCss() {
    css {
        display = Display.flex
        flexDirection = FlexDirection.row
        alignItems = AlignItems.center
        margin = Margin(vertical = 0.px, horizontal = Auto.auto)
    }
}

private fun List<CountedResource>.toRepeatedTypesList(): List<ResourceType> = flatMap { cr -> List(cr.count) { cr.type } }
