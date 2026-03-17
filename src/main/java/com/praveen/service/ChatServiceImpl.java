package com.praveen.service;

import com.praveen.dto.ChatMessage;
import com.praveen.entities.ChatMessageEntity;
import com.praveen.repository.ChatMessageRepository;
import com.praveen.repository.EmployeeRepository;
import com.praveen.repository.StudentRepository;
import com.praveen.service.ChatService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	@Autowired
    private final ChatMessageRepository repository;
    
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<ChatMessageEntity> getConversation(String user1, String user2) {

        return repository
                .findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
                        user1, user2, user2, user1
                );
    }
    
    @Override
    public List<String> getConversationUsers(String hrEmail) {

        List<ChatMessageEntity> messages =
        		repository.findBySenderIdOrReceiverId(hrEmail, hrEmail);

        Set<String> students = new HashSet<>();

        for(ChatMessageEntity m : messages){

            if(m.getSenderId().equals(hrEmail)){
                students.add(m.getReceiverId());
            }else{
                students.add(m.getSenderId());
            }

        }

        return new ArrayList<>(students);
    }
    
    public List<String> getConversationsByRole(String hrEmail, String role){

        List<ChatMessageEntity> messages =
                repository.findBySenderIdOrReceiverId(hrEmail, hrEmail);

        Set<String> users = new HashSet<>();

        for(ChatMessageEntity m : messages){

            String otherUser =
                    m.getSenderId().equals(hrEmail)
                    ? m.getReceiverId()
                    : m.getSenderId();

            if(users.contains(otherUser)) continue;

            String detectedRole = getUserRole(otherUser);

            if(role.equals(detectedRole)){
                users.add(otherUser);
            }

        }

        return new ArrayList<>(users);
    }
    
    private String getUserRole(String email){

        if(studentRepository.existsByUserEmail(email)){
            return "STUDENT";
        }

        if(employeeRepository.existsByUserEmail(email)){
            return "PANEL";
        }

        return "UNKNOWN";
    }
}