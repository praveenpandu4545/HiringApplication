package com.praveen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.praveen.entities.NoticeAttachment;

@Repository
public interface NoticeAttachmentRepository
        extends JpaRepository<NoticeAttachment, Long> {

}