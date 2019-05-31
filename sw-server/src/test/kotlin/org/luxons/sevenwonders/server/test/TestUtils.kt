package org.luxons.sevenwonders.server.test

import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate

fun mockSimpMessagingTemplate(): SimpMessagingTemplate = SimpMessagingTemplate(object : MessageChannel {
    override fun send(message: Message<*>): Boolean = true
    override fun send(message: Message<*>, timeout: Long): Boolean = true
})
