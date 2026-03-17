package com.praveen.repository;

import com.praveen.entities.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatFileRepository extends JpaRepository<ChatFile, Long> {
}