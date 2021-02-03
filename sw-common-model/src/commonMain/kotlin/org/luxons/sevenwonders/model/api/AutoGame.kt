package org.luxons.sevenwonders.model.api

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.TableState
import org.luxons.sevenwonders.model.api.actions.BotConfig
import org.luxons.sevenwonders.model.score.ScoreBoard
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import kotlin.random.Random

@Serializable
data class AutoGameAction(
    val nbPlayers: Int = 3,
    val gameName: String = "AutoGame-${Random.nextInt().toString(16)}",
    val customSettings: Settings? = null,
    val customWonders: List<AssignedWonder>? = null,
    /**
     * The configuration of the bots that will play the game.
     */
    val config: BotConfig = BotConfig(0, 0),
)

@Serializable
data class AutoGameResult(
    val scoreBoard: ScoreBoard,
    val table: TableState,
)
