package org.luxons.sevenwonders.test.api

import java.util.Objects

class ApiPlayer {

    val username: String? = null

    var displayName: String? = null

    var index: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val apiPlayer = other as ApiPlayer?
        return index == apiPlayer!!.index && username == apiPlayer.username && displayName == apiPlayer.displayName
    }

    override fun hashCode(): Int {
        return Objects.hash(username, displayName, index)
    }
}
