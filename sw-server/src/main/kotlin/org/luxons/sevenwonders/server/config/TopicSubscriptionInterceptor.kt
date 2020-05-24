package org.luxons.sevenwonders.server.config

import org.luxons.sevenwonders.server.validation.DestinationAccessValidator
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component

@Component
class TopicSubscriptionInterceptor(
    private val destinationAccessValidator: DestinationAccessValidator
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val headerAccessor = StompHeaderAccessor.wrap(message)
        if (StompCommand.SUBSCRIBE == headerAccessor.command) {
            val username = headerAccessor.user!!.name
            val destination = headerAccessor.destination!!
            if (!destinationAccessValidator.hasAccess(username, destination)) {
                sendForbiddenSubscriptionError(username, destination)
                return null
            }
        }
        return message
    }

    private fun sendForbiddenSubscriptionError(username: String, destination: String?) {
        logger.error(String.format("Player '%s' is not allowed to access %s", username, destination))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TopicSubscriptionInterceptor::class.java)
    }
}
