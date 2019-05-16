package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.cards.TableCard
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.wonders.ApiWonder

data class Board(
    val playerIndex: Int,
    val wonder: ApiWonder,
    val production: ApiProduction,
    val publicProduction: ApiProduction,
    val science: ApiScience,
    val military: ApiMilitary,
    val playedCards: List<List<TableCard>>,
    val gold: Int
)

data class ApiRequirements(
    val gold: Int = 0,
    val resources: List<ApiCountedResource> = emptyList()
)

data class ApiProduction(
    val fixedResources: List<ApiCountedResource>,
    val alternativeResources: Set<Set<ResourceType>>
)

data class ApiCountedResource(
    val count: Int,
    val type: ResourceType
)

data class ApiMilitary(
    val nbShields: Int,
    val totalPoints: Int,
    val nbDefeatTokens: Int
)

data class ApiScience(
    val jokers: Int,
    val nbWheels: Int,
    val nbCompasses: Int,
    val nbTablets: Int
)
