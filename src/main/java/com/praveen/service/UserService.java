package com.praveen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Long getUserIdFromUsername(String username) {

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
