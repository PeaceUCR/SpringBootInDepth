package com.example.demo.controller;

import com.example.demo.entity.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    //The @MessageMapping annotation ensures that if a message is sent to destination "/chat.sendMessage", then the sendMessage() method is called.
    //The return value is broadcast to all subscribers to "/topic/public" as specified in the @SendTo annotation.

    //at client
    /*
     stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
     client send msg will me handled by this method
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    //at client
    /*
        stompClient.send("/app/chat.addUser",
            {},
            JSON.stringify({sender: username, type: 'JOIN'})
        )
        will be handled by this method

        The return value is broadcast to all subscribers to "/topic/public" as specified in the @SendTo annotation.
    */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

}
