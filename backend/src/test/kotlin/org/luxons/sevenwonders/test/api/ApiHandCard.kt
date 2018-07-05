package org.luxons.sevenwonders.test.api

/**
 * A card with contextual information relative to the hand it is sitting in. The extra information is especially
 * useful because it frees the client from a painful business logic implementation.
 */
class ApiHandCard {

    var card: ApiCard? = null

    var isChainable: Boolean = false

    var isFree: Boolean = false

    var isPlayable: Boolean = false
}
