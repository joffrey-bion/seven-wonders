package org.luxons.sevenwonders.game.data

import com.github.salomonbrys.kotson.typeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.luxons.sevenwonders.game.Game
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.Age
import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.data.definitions.DecksDefinition
import org.luxons.sevenwonders.game.data.definitions.WonderDefinition
import org.luxons.sevenwonders.game.data.serializers.NumericEffectSerializer
import org.luxons.sevenwonders.game.data.serializers.ProductionIncreaseSerializer
import org.luxons.sevenwonders.game.data.serializers.ProductionSerializer
import org.luxons.sevenwonders.game.data.serializers.ResourceTypeSerializer
import org.luxons.sevenwonders.game.data.serializers.ResourceTypesSerializer
import org.luxons.sevenwonders.game.data.serializers.ResourcesSerializer
import org.luxons.sevenwonders.game.data.serializers.ScienceProgressSerializer
import org.luxons.sevenwonders.game.effects.GoldIncrease
import org.luxons.sevenwonders.game.effects.MilitaryReinforcements
import org.luxons.sevenwonders.game.effects.ProductionIncrease
import org.luxons.sevenwonders.game.effects.RawPointsIncrease
import org.luxons.sevenwonders.game.effects.ScienceProgress
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.api.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources

internal const val LAST_AGE: Age = 3

internal data class GlobalRules(
    val minPlayers: Int,
    val maxPlayers: Int
)

class GameDefinition internal constructor(
    rules: GlobalRules,
    private val wonders: List<WonderDefinition>,
    private val decksDefinition: DecksDefinition
) {
    val minPlayers: Int = rules.minPlayers
    val maxPlayers: Int = rules.maxPlayers

    fun initGame(id: Long, customSettings: CustomizableSettings, nbPlayers: Int): Game {
        val settings = Settings(nbPlayers, customSettings)
        val boards = assignBoards(settings, nbPlayers)
        val decks = decksDefinition.prepareDecks(settings.nbPlayers, settings.random)
        return Game(id, settings, boards, decks)
    }

    private fun assignBoards(settings: Settings, nbPlayers: Int): List<Board> {
        return wonders.shuffled(settings.random)
            .take(nbPlayers)
            .mapIndexed { i, wDef -> Board(wDef.create(settings.pickWonderSide()), i, settings) }
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
