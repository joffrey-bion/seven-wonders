package org.luxons.sevenwonders.engine.test

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.boards.Science
import org.luxons.sevenwonders.engine.boards.ScienceType
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.cards.Requirements
import org.luxons.sevenwonders.engine.effects.Effect
import org.luxons.sevenwonders.engine.effects.ScienceProgress
import org.luxons.sevenwonders.engine.moves.Move
import org.luxons.sevenwonders.engine.moves.resolve
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.engine.resources.resourcesOf
import org.luxons.sevenwonders.engine.wonders.Wonder
import org.luxons.sevenwonders.engine.wonders.WonderStage
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.Color
import org.luxons.sevenwonders.model.resources.*

internal const val SEED: Long = 42

internal fun testSettings(initialGold: Int = 0): Settings = Settings(
    randomSeedForTests = SEED,
    initialGold = initialGold
)

internal fun testTable(nbPlayers: Int = 5): Table = testTable(nbPlayers, testSettings())

internal fun testTable(nbPlayers: Int, settings: Settings): Table = Table(testBoards(nbPlayers, settings))

private fun testBoards(count: Int, settings: Settings): List<Board> = List(count) { testBoard(settings) }

internal fun testBoard(
    initialResource: ResourceType = ResourceType.WOOD,
    initialGold: Int = 0,
    vararg production: ResourceType
): Board {
    val settings = testSettings(initialGold = initialGold)
    val board = testBoard(settings, initialResource)
    board.production.addAll(fixedProduction(*production))
    return board
}

private fun testBoard(settings: Settings, initialResource: ResourceType = ResourceType.WOOD): Board =
    Board(testWonder(initialResource), 0, settings)

internal fun testWonder(initialResource: ResourceType = ResourceType.WOOD): Wonder {
    val stage1 = WonderStage(Requirements(), emptyList())
    val stage2 = WonderStage(Requirements(), emptyList())
    val stage3 = WonderStage(Requirements(), emptyList())
    return Wonder("Test Wonder ${initialResource.symbol}", initialResource, listOf(stage1, stage2, stage3), "")
}

internal fun fixedProduction(vararg producedTypes: ResourceType): Production =
    Production().apply { addAll(resourcesOf(*producedTypes)) }

internal fun createTransactions(provider: Provider, vararg resources: ResourceType): ResourceTransactions =
    createTransactions(createTransaction(provider, *resources))

internal fun createTransactions(vararg transactions: ResourceTransaction): ResourceTransactions = transactions.toSet()

internal fun createTransaction(provider: Provider, vararg resources: ResourceType): ResourceTransaction =
    ResourceTransaction(provider, resources.map { CountedResource(1, it) })

internal fun createRequirements(vararg types: ResourceType): Requirements = Requirements(resources = resourcesOf(*types))

internal fun sampleCards(nbCards: Int, fromIndex: Int = 0, color: Color = Color.BLUE): List<Card> =
    List(nbCards) { i -> testCard("Test Card ${fromIndex + i}", color) }

internal fun createGuildCards(count: Int): List<Card> = List(count) { createGuildCard(it) }

internal fun createGuildCard(num: Int, effect: Effect? = null): Card =
    testCard("Test Guild $num", Color.PURPLE, effect = effect)

internal fun testCard(
    name: String = "Test Card",
    color: Color = Color.BLUE,
    requirements: Requirements = Requirements(),
    effect: Effect? = null
): Card {
    val effects = if (effect == null) emptyList() else listOf(effect)
    return Card(name, color, requirements, effects, null, emptyList(), "path/to/card/image", CardBack("image-III"))
}

internal fun addCards(board: Board, nbCardsOfColor: Int, nbOtherCards: Int, color: Color) {
    addCards(board, nbCardsOfColor, color)
    addCards(board, nbOtherCards, getDifferentColorFrom(color))
}

internal fun addCards(board: Board, nbCards: Int, color: Color) {
    sampleCards(nbCards, color = color).forEach { board.addCard(it) }
}

internal fun getDifferentColorFrom(vararg colors: Color): Color =
    Color.values().firstOrNull { it !in colors } ?: throw IllegalArgumentException("All colors are forbidden!")

internal fun createScienceProgress(compasses: Int, wheels: Int, tablets: Int, jokers: Int): ScienceProgress =
    ScienceProgress(createScience(compasses, wheels, tablets, jokers))

internal fun createScience(compasses: Int, wheels: Int, tablets: Int, jokers: Int): Science = Science().apply {
    add(ScienceType.COMPASS, compasses)
    add(ScienceType.WHEEL, wheels)
    add(ScienceType.TABLET, tablets)
    addJoker(jokers)
}

internal fun playCardWithEffect(player: Player, color: Color, effect: Effect) {
    val card = testCard(color = color, effect = effect)
    player.board.addCard(card)
    card.applyTo(player, noTransactions())
}

internal fun createMove(context: PlayerContext, card: Card, type: MoveType): Move =
    type.resolve(PlayerMove(type, card.name), card, context, emptyList())

internal fun singleBoardPlayer(board: Board): Player = object : Player {
    override val index = 0
    override val board = board
    override fun getBoard(relativePosition: RelativeBoardPosition): Board = when (relativePosition) {
        RelativeBoardPosition.LEFT -> throw RuntimeException("No LEFT board")
        RelativeBoardPosition.SELF -> this.board
        RelativeBoardPosition.RIGHT -> throw RuntimeException("No RIGHT board")
    }
}
