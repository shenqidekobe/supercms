package com.dw.suppercms.infrastructure.web.websocket;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.dw.suppercms.infrastructure.common.ConstantConfig;
import com.dw.suppercms.infrastructure.web.websocket.interceptor.HandshakeInterceptor;

/**
 * websocket congifuration
 * 
 * @ServiceScan(basePackages={"com.dw.suppercms.infrastructure.web.websocket"})   
 * 
 * 采用 java 代码排至的方式
 *  spring xml配置方式：
 *   <websocket:handlers>
        <websocket:mapping path="/makeFileSocket" handler="makeFileSocket"/>
         <websocket:sockjs/>
        <websocket:handshake-interceptors>
            <bean class="com.dw.suppercms.infrastructure.web.websocket。HandshakeInterceptor"/>
        </websocket:handshake-interceptors>
    </websocket:handlers>
    <bean id="makeFileSocket" class="com.dw.suppercms.infrastructure.web.websocket.MakeFileWebSocketHandler"/>
 * */

@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer{
	
	@Resource
	private ConstantConfig constantConfig;
	
	public WebSocketConfig(){}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getMakeFileWebSocketHandler(),"/endpoint/makeFileSocket")
		                     .addInterceptors(getHandshakeInterceptor())
		                     .setAllowedOrigins(constantConfig.getHost());
	    registry.addHandler(getMakeFileWebSocketHandler(), "/endpoint/sockjs/makeFileSocket")
	                         .addInterceptors(getHandshakeInterceptor()).withSockJS();
	}
	
	@Bean
	public MakeFileWebSocketHandler getMakeFileWebSocketHandler(){
		return new MakeFileWebSocketHandler();
	}
	
	@Bean
	public HandshakeInterceptor getHandshakeInterceptor(){
		return new HandshakeInterceptor();
	}
	
}
