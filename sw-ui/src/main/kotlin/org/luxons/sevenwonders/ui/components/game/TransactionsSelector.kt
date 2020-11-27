package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.*
import kotlinx.css.*
import kotlinx.html.DIV
import kotlinx.html.TD
import kotlinx.html.classes
import kotlinx.html.js.onClickFunction
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.resources.Provider
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.ui.redux.TransactionSelectorState
import react.RBuilder
import react.dom.div
import react.dom.p
import react.dom.tbody
import styled.*

fun RBuilder.transactionsSelectorDialog(
    state: TransactionSelectorState?,
    prepareMove: (PlayerMove) -> Unit,
    cancelTransactionSelection: () -> Unit,
) {
    bpDialog(
        isOpen = state != null,
        title = "Time to foot the bill!",
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
                    bpIcon("user", size = 50)
                    styledDiv {
                        css {
                            grow(Grow.GROW)
                            margin(horizontal = 0.5.rem)
                        }
                        optionsTable(state, prepareMove)
                    }
                    bpIcon("user", size = 50)
                }
            }
        }
    }
}

private fun RBuilder.optionsTable(
    state: TransactionSelectorState,
    prepareMove: (PlayerMove) -> Unit,
) {
    bpHtmlTable(interactive = true) {
        tbody {
            state.transactionsOptions.forEach { transactions ->
                styledTr {
                    css {
                        cursor = Cursor.pointer
                        alignItems = Align.center
                    }
                    attrs {
                        onClickFunction = { prepareMove(PlayerMove(state.moveType, state.card.name, transactions)) }
                    }
                    // there should be at most one of each
                    val left = transactions.firstOrNull { it.provider == Provider.LEFT_PLAYER }
                    val right = transactions.firstOrNull { it.provider == Provider.RIGHT_PLAYER }
                    styledTd {
                        transactionCellCss()
                        if (left != null) {
                            styledDiv {
                                transactionCellInnerCss()
                                bpIcon(name = left.provider.arrowIcon(), size = Icon.SIZE_LARGE)
                                goldIndicator(left.totalPrice, imgSize = 2.rem)
                            }
                        }
                    }
                    styledTd {
                        transactionCellCss()
                        if (left != null) {
                            styledDiv {
                                transactionCellInnerCss()
                                resourceList(left.resources)
                            }
                        }
                    }
                    styledTd {
                        transactionCellCss()
                        css {
                            // make this cell fill the space
                            width = 100.pct
                        }
//                        goldIndicator(-transactions.sumBy { it.totalPrice }) {
//                            css {
//                                width = LinearDimension.fitContent
//                                margin(horizontal = LinearDimension.auto)
//                            }
//                        }
                    }
                    styledTd {
                        transactionCellCss()
                        if (right != null) {
                            styledDiv {
                                transactionCellInnerCss()
                                resourceList(right.resources)
                            }
                        }
                    }
                    styledTd {
                        transactionCellCss()
                        if (right != null) {
                            styledDiv {
                                transactionCellInnerCss()
                                goldIndicator(right.totalPrice, imgSize = 2.rem)
                                bpIcon(name = right.provider.arrowIcon(), size = Icon.SIZE_LARGE)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun StyledDOMBuilder<TD>.transactionCellCss() {
    // we need inline styles to win over blueprintjs's styles (which are more specific than .class)
    inlineStyles {
        verticalAlign = VerticalAlign.middle
    }
}

private fun StyledDOMBuilder<DIV>.transactionCellInnerCss() {
    css {
        display = Display.flex
        flexDirection = FlexDirection.row
        alignItems = Align.center
    }
}

private fun Provider.arrowIcon() = when (this) {
    Provider.LEFT_PLAYER -> "caret-left"
    Provider.RIGHT_PLAYER -> "caret-right"
}

private fun RBuilder.resourceList(resources: List<CountedResource>) {
    // The biggest card is the Palace and requires 7 resources (1 of each).
    // We always have at least 1 resource on our wonder, so we'll never need to buy more than 6.
    // Therefore, 3 by row seems decent.
    val rows = resources.toRepeatedTypesList().chunked(3)
    styledDiv {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = Align.center
            grow(Grow.GROW)
        }
        rows.forEach { row ->
            styledDiv {
                resourceRowCss()
                row.forEach {
                    resourceImage(it, size = 1.5.rem)
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
