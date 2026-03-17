package com.praveen.controller;

import com.praveen.entities.ChatMessageEntity;
import com.praveen.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService service;

    @GetMapping("/history")
    public List<ChatMessageEntity> history(
            @RequestParam String user1,
            @RequestParam String user2){
        return service.getConversation(user1,user2);
    }
    
    @GetMapping("/conversations")
    public List<String> getConversations(
            @RequestParam String role,
            Authentication authentication
    ){

        String hrEmail = authentication.getName();

        return service.getConversationsByRole(hrEmail, role);
    }
}