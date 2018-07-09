package org.luxons.sevenwonders.game.data

typealias Age = Int

const val LAST_AGE: Age = 3

internal data class GlobalRules(
    val minPlayers: Int,
    val maxPlayers: Int
)
