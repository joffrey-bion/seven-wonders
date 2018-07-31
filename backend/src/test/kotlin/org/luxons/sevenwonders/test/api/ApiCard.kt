package org.luxons.sevenwonders.test.api

import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.Requirements
import java.util.Objects

class ApiCard {

    var name: String? = null

    var color: Color? = null

    var requirements: Requirements? = null

    var chainParent: String? = null

    var chainChildren: List<String>? = null

    var image: String? = null

    var back: ApiCardBack? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val apiCard = other as ApiCard?
        return name == apiCard!!.name && color === apiCard.color && requirements == apiCard.requirements && chainParent == apiCard.chainParent && chainChildren == apiCard.chainChildren && image == apiCard.image && back == apiCard.back
    }

    override fun hashCode(): Int {
        return Objects.hash(name, color, requirements, chainParent, chainChildren, image, back)
    }
}
