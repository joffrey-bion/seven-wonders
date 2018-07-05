package org.luxons.sevenwonders.test

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate

object TestUtils {

    fun createSimpMessagingTemplate(): SimpMessagingTemplate {
        val messageChannel = object : MessageChannel {
            override fun send(message: Message<*>): Boolean {
                return true
            }

            override fun send(message: Message<*>, timeout: Long): Boolean {
                return true
            }
        }
        return SimpMessagingTemplate(messageChannel)
    }
}
