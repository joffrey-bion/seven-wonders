package org.luxons.sevenwonders.engine.boards

enum class ScienceType {
    COMPASS,
    WHEEL,
    TABLET
}

internal class Science {

    private val quantities: MutableMap<ScienceType, Int> = mutableMapOf()

    var jokers: Int = 0
        private set

    fun size(): Int = quantities.values.sum() + jokers

    fun add(type: ScienceType, quantity: Int) {
        quantities.merge(type, quantity) { x, y -> x + y }
    }

    fun addJoker(quantity: Int) {
        jokers += quantity
    }

    fun addAll(science: Science) {
        science.quantities.forEach { (type, quantity) -> this.add(type, quantity) }
        jokers += science.jokers
    }

    fun getQuantity(type: ScienceType): Int = quantities.getOrDefault(type, 0)

    fun computePoints(): Int {
        val values = ScienceType.values().map(::getQuantity).toMutableList()
        return computePoints(values, jokers)
    }

    private fun computePoints(values: MutableList<Int>, jokers: Int): Int {
        if (jokers == 0) {
            return computePointsNoJoker(values)
        }
        var maxPoints = 0
        for (i in values.indices) {
            values[i]++
            maxPoints = maxPoints.coerceAtLeast(computePoints(values, jokers - 1))
            values[i]--
        }
        return maxPoints
    }

    private fun computePointsNoJoker(values: List<Int>): Int {
        val independentSquaresSum = values.sumBy { it * it }
        val nbGroupsOfAll = values.minOrNull() ?: 0
        return independentSquaresSum + nbGroupsOfAll * 7
    }
}
