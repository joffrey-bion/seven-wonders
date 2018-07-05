package org.luxons.sevenwonders.test.api

import java.util.Objects

class ApiPlayer {

    val username: String? = null

    var displayName: String? = null

    var index: Int = 0

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val apiPlayer = o as ApiPlayer?
        return index == apiPlayer!!.index && username == apiPlayer.username && displayName == apiPlayer.displayName
    }

    override fun hashCode(): Int {
        return Objects.hash(username, displayName, index)
    }
}
