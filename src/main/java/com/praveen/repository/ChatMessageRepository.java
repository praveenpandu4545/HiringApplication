package com.praveen.repository;

import com.praveen.dto.ChatMessage;
import com.praveen.entities.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity>
    findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
            String sender1,
            String receiver1,
            String sender2,
            String receiver2
    );
    
    List<ChatMessageEntity> findBySenderIdOrReceiverId(String senderId, String receiverId);
}