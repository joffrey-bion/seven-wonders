package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.cards.*
import org.luxons.sevenwonders.ui.components.*
import react.*
import react.dom.html.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import web.cssom.*
import web.cssom.Position
import web.html.*

fun ChildrenBuilder.preparedMove(
    card: HandCard,
    move: PlayerMove,
    unprepareMove: () -> Unit,
    block: HTMLAttributes<HTMLDivElement>.() -> Unit,
) {
    div {
        block()
        CardImage {
            this.card = card
            if (move.type == MoveType.DISCARD || move.type == MoveType.UPGRADE_WONDER) {
                this.className = GameStyles.dimmedCard
            }
        }
        if (move.type == MoveType.DISCARD) {
            discardText()
        }
        if (move.type == MoveType.UPGRADE_WONDER) {
            upgradeWonderSymbol()
        }
        div {
            css {
                position = web.cssom.Position.absolute
                top = 0.px
                right = 0.px
            }
            BpButton {
                icon = IconNames.CROSS
                title = "Cancel prepared move"
                small = true
                intent = Intent.DANGER
                onClick = { unprepareMove() }
            }
        }
    }
}

private fun ChildrenBuilder.discardText() {
    div {
        css(GlobalStyles.centerInPositionedParent, GameStyles.discardMoveText) {}
        +"DISCARD"
    }
}

private fun ChildrenBuilder.upgradeWonderSymbol() {
    img {
        src = "/images/wonder-upgrade-bright.png"
        css {
            width = 8.rem
            position = Position.absolute
            left = 10.pct
            top = 50.pct
            transform = translate((-50).pct, (-50).pct)
        }
    }
}
