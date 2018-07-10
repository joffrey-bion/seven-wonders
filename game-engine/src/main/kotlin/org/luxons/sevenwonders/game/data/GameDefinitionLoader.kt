package org.luxons.sevenwonders.game.data

import com.github.salomonbrys.kotson.typeToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources

class GameDefinitionLoader {

    val gameDefinition: GameDefinition by lazy { load() }

    private fun load(): GameDefinition {
        val gson: Gson = createGson()
        val rules = loadJson("global_rules.json", GlobalRules::class.java, gson)
        val wonders = loadJson("wonders.json", Array<WonderDefinition>::class.java, gson)
        val decksDefinition = loadJson("cards.json", DecksDefinition::class.java, gson)
        return GameDefinition(rules, wonders, decksDefinition)
    }

    private fun <T> loadJson(filename: String, clazz: Class<T>, gson: Gson): T {
        val packageAsPath = GameDefinitionLoader::class.java.`package`.name.replace('.', '/')
        val resourcePath = "/$packageAsPath/$filename"
        val resource = GameDefinitionLoader::class.java.getResource(resourcePath)
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

    private inline fun <reified T: Any> GsonBuilder.registerTypeAdapter(typeAdapter: Any): GsonBuilder
            = this.registerTypeAdapter(typeToken<T>(), typeAdapter)
}
