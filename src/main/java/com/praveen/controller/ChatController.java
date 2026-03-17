package com.praveen.controller;

import com.praveen.dto.ChatMessage;
import com.praveen.entities.ChatMessageEntity;
import com.praveen.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository repository;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message){

        ChatMessageEntity entity = ChatMessageEntity.builder()
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .content(message.getContent())
                .type(message.getType())
                .fileId(message.getFileId())
                .timestamp(System.currentTimeMillis())
                .build();

        repository.save(entity);

        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                message
        );

        messagingTemplate.convertAndSend(
                "/topic/new-student",
                message.getSenderId()
        );
    }
}