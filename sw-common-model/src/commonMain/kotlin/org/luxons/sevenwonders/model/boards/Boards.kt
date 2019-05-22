package org.luxons.sevenwonders.model.boards

import org.luxons.sevenwonders.model.cards.TableCard
import org.luxons.sevenwonders.model.resources.CountedResource
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.wonders.ApiWonder

data class ApiBoard(
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
    val resources: List<CountedResource> = emptyList()
)

data class ApiProduction(
    val fixedResources: List<CountedResource>,
    val alternativeResources: Set<Set<ResourceType>>
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
