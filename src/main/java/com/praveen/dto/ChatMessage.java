package com.praveen.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    private String senderId;
    private String receiverId;
    private String content;
    private MessageType type;
    private Long fileId;
    public enum MessageType {
        CHAT,
        FILE
    }
}