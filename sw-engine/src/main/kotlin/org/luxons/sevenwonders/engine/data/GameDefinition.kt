package org.luxons.sevenwonders.engine.data

import com.github.salomonbrys.kotson.typeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.luxons.sevenwonders.engine.Game
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.data.definitions.DecksDefinition
import org.luxons.sevenwonders.engine.data.definitions.WonderDefinition
import org.luxons.sevenwonders.engine.data.serializers.NumericEffectSerializer
import org.luxons.sevenwonders.engine.data.serializers.ProductionIncreaseSerializer
import org.luxons.sevenwonders.engine.data.serializers.ProductionSerializer
import org.luxons.sevenwonders.engine.data.serializers.ResourceTypeSerializer
import org.luxons.sevenwonders.engine.data.serializers.ResourceTypesSerializer
import org.luxons.sevenwonders.engine.data.serializers.ResourcesSerializer
import org.luxons.sevenwonders.engine.data.serializers.ScienceProgressSerializer
import org.luxons.sevenwonders.engine.effects.GoldIncrease
import org.luxons.sevenwonders.engine.effects.MilitaryReinforcements
import org.luxons.sevenwonders.engine.effects.ProductionIncrease
import org.luxons.sevenwonders.engine.effects.RawPointsIncrease
import org.luxons.sevenwonders.engine.effects.ScienceProgress
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.engine.resources.Resources
import org.luxons.sevenwonders.model.Age
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import org.luxons.sevenwonders.model.wonders.PreGameWonder

internal const val LAST_AGE: Age = 3

internal data class GlobalRules(
    val minPlayers: Int,
    val maxPlayers: Int,
)

class GameDefinition internal constructor(
    rules: GlobalRules,
    wonderDefinitions: List<WonderDefinition>,
    private val decksDefinition: DecksDefinition,
) {
    val minPlayers: Int = rules.minPlayers
    val maxPlayers: Int = rules.maxPlayers

    val allWonders: List<PreGameWonder> = wonderDefinitions.map { w ->
        PreGameWonder(w.name, w.sides.mapValues { (_, def) -> def.image })
    }

    private val wondersByName = wonderDefinitions.associateBy { it.name }

    fun createGame(id: Long, wonders: Collection<AssignedWonder>, settings: Settings): Game {
        val nbPlayers = wonders.size
        val boards = wonders.mapIndexed { index, wonder -> wonder.createBoard(index, settings) }
        val decks = decksDefinition.prepareDecks(nbPlayers, settings.random)
        return Game(id, settings, boards, decks)
    }

    private fun AssignedWonder.createBoard(playerIndex: Int, settings: Settings): Board {
        val wonder = wondersByName[name] ?: error("Unknown wonder '$name'")
        return Board(wonder.create(side), playerIndex, settings)
    }

    companion object {

        fun load(): GameDefinition {
            val gson: Gson = createGson()
            val rules = loadJson("global_rules.json", GlobalRules::class.java, gson)
            val wonders = loadJson("wonders.json", Array<WonderDefinition>::class.java, gson)
            val decksDefinition = loadJson("cards.json", DecksDefinition::class.java, gson)
            return GameDefinition(rules, wonders.toList(), decksDefinition)
        }
    }
}

private fun <T> loadJson(filename: String, clazz: Class<T>, gson: Gson): T {
    val packageAsPath = GameDefinition::class.java.`package`.name.replace('.', '/')
    val resourcePath = "/$packageAsPath/$filename"
    val resource = GameDefinition::class.java.getResource(resourcePath)
    val json = resource.readText()
    return gson.fromJson(json, clazz)
}

private fun createGson(): Gson {
    return GsonBuilder().disableHtmlEscaping()
        .registerTypeAdapter<Resources>(ResourcesSerializer())
        .registerTypeAdapter<ResourceType>(ResourceTypeSerializer())
        .registerTypeAdapter<List<ResourceType>>(ResourceTypesSerializer())
        .registerTypeAdapter<Production>(ProductionSerializer())
        .registerTypeAdapter<ProductionIncrease>(ProductionIncreaseSerializer())
        .registerTypeAdapter<MilitaryReinforcements>(NumericEffectSerializer())
        .registerTypeAdapter<RawPointsIncrease>(NumericEffectSerializer())
        .registerTypeAdapter<GoldIncrease>(NumericEffectSerializer())
        .registerTypeAdapter<ScienceProgress>(ScienceProgressSerializer())
        .create()
}

private inline fun <reified T : Any> GsonBuilder.registerTypeAdapter(typeAdapter: Any): GsonBuilder =
    this.registerTypeAdapter(typeToken<T>(), typeAdapter)
