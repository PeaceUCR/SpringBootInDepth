package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//The @EnableWebSocketMessageBroker is used to enable our WebSocket server.
//enables WebSocket message handling, backed by a message broker.
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // STOMP stands for Simple Text Oriented Messaging Protocol. It is a messaging protocol that defines the format and rules for data exchange.
    //we’re configuring a message broker that will be used to route messages from one client to another

    //add end point here to let client to connect
    //in main.js
    //     var socket = new SockJS('/ws');
    //     stompClient = Stomp.over(socket);
    //     stompClient.connect({}, onConnected, onError);
    //name 'ws' is the end point here

    //Notice the use of withSockJS() with the endpoint configuration. SockJS is used to enable fallback options for browsers that don’t support websocket.
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws");
        registry.addEndpoint("/ws").withSockJS();
    }


    //we’re configuring a message broker that will be used to route messages from one client to another.

    //the first line defines that the messages whose destination starts with “/topic” should be routed to the message broker. Message broker broadcasts messages to all the connected clients who are subscribed to a particular topic.
    //The second line defines that the messages whose destination starts with “/app” should be routed to message-handling methods
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        //to enable a simple memory-based message broker to carry the messages back to the client on destinations prefixed with "/topic".
        config.enableSimpleBroker("/topic");

        // It also designates the "/app" prefix for messages that are bound for @MessageMapping-annotated methods. This prefix will be used to define all the message mappings;
        config.setApplicationDestinationPrefixes("/app");
    }


}
