package org.luxons.sevenwonders.game.data

import org.luxons.sevenwonders.game.Game
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition
import org.luxons.sevenwonders.game.data.definitions.WonderDefinition

class GameDefinition internal constructor(
    rules: GlobalRules,
    private val wonders: Array<WonderDefinition>,
    private val decksDefinition: DecksDefinition
) {
    val minPlayers: Int = rules.minPlayers
    val maxPlayers: Int = rules.maxPlayers

    fun initGame(id: Long, customSettings: CustomizableSettings, nbPlayers: Int): Game {
        val settings = Settings(nbPlayers, customSettings)
        val boards = assignBoards(settings, nbPlayers)
        val decks = decksDefinition.prepareDecks(settings)
        return Game(id, settings, boards, decks)
    }

    private fun assignBoards(settings: Settings, nbPlayers: Int): List<Board> {
        val randomizedWonders = wonders.toMutableList()
        randomizedWonders.shuffle(settings.random)
        return randomizedWonders.take(nbPlayers).mapIndexed { i, wDef -> Board(wDef.create(settings), i, settings) }
    }
}
