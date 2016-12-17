package org.luxons.sevenwonders.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import sun.security.acl.PrincipalImpl;

class AnonymousUsersHandshakeHandler extends DefaultHandshakeHandler {

    private int playerId = 0;

    @Override
    public Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        Principal p = request.getPrincipal();
        if (p == null) {
            p = new PrincipalImpl("player" + playerId++);
        }
        return p;
    }
}
