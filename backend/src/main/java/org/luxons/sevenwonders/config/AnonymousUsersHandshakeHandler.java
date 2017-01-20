package org.luxons.sevenwonders.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

class AnonymousUsersHandshakeHandler extends DefaultHandshakeHandler {

    private int playerId = 0;

    @Override
    public Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        Principal p = super.determineUser(request, wsHandler, attributes);
        if (p == null) {
            p = new UsernamePasswordAuthenticationToken("player" + playerId++, null);
        }
        return p;
    }
}
