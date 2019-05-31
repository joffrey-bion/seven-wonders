package org.luxons.sevenwonders.server.config

import org.springframework.http.server.ServerHttpRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal

/**
 * Generates [Principal] objects for anonymous users in the form "playerX", where X is an auto-incremented number.
 */
internal class AnonymousUsersHandshakeHandler : DefaultHandshakeHandler() {

    private var playerId = 0

    override fun determineUser(
        request: ServerHttpRequest,
        wsHandler: WebSocketHandler,
        attributes: Map<String, Any>
    ): Principal? {
        var p = super.determineUser(request, wsHandler, attributes)
        if (p == null) {
            p = UsernamePasswordAuthenticationToken("player" + playerId++, null)
        }
        return p
    }
}
