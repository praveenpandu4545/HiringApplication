package com.praveen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.praveen.dto.NoticeBoardDTO;
import com.praveen.entities.NoticeAttachment;
import com.praveen.entities.NoticeBoard;
import com.praveen.repository.NoticeAttachmentRepository;
import com.praveen.service.NoticeBoardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/springApi/notice")
public class NoticeController {
	
	@Autowired
	public NoticeBoardService noticeBoardService;
	
	@Autowired
	public NoticeAttachmentRepository noticeAttachmentRepository;
	
	@PostMapping("/create-notice")
	public ResponseEntity<String> createNotice(
	        @ModelAttribute NoticeBoardDTO dto) {

	    noticeBoardService.createNotice(dto);

	    return ResponseEntity.ok("Notice created successfully");
	}
	
	@GetMapping("/get-all-notices")
	public ResponseEntity<List<NoticeBoard>> getAllNotices() {

	    return ResponseEntity.ok(
	            noticeBoardService.getAllNotices()
	    );
	}
	
	@GetMapping("/attachment/{id}")
	public ResponseEntity<byte[]> downloadAttachment(
	        @PathVariable Long id) {

	    NoticeAttachment attachment =
	            noticeAttachmentRepository
	                    .findById(id)
	                    .orElseThrow(() ->
	                            new RuntimeException(
	                                    "Attachment not found"
	                            )
	                    );

	    return ResponseEntity.ok()
	            .header(
	                    HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=\"" +
	                            attachment.getFileName() + "\""
	            )
	            .contentType(
	                    MediaType.parseMediaType(
	                            attachment.getContentType()
	                    )
	            )
	            .body(attachment.getData());
	}
}
