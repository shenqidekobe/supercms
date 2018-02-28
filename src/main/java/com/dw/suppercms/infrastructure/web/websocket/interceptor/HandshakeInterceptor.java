package com.dw.suppercms.infrastructure.web.websocket.interceptor;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.dw.suppercms.domain.security.User;
import com.dw.suppercms.infrastructure.utils.CommonsUtil;

/**
 * Websocket握手拦截器之获取session信息
 * */
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor{
	
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            User user=CommonsUtil.getPrincipal();
            if (user != null) {
                attributes.put("user",user);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception ex) {
    }

}
