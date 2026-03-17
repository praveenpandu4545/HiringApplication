package com.praveen.socket;

import com.praveen.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtUtil jwtUtil;

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        String query = request.getURI().getQuery();

        if(query == null){
            return null;
        }

        String token = query.replace("token=","");

        String email = jwtUtil.extractEmail(token);

        return () -> email;
    }
}