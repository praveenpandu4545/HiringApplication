package com.praveen.service;

import com.praveen.entities.ChatMessageEntity;

import java.util.List;

public interface ChatService {

    List<ChatMessageEntity> getConversation(String user1, String user2);
    List<String> getConversationUsers(String hrEmail);
    List<String> getConversationsByRole(String hrEmail, String role);
}