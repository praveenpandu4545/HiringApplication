package com.praveen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.praveen.dto.StudentRoundStatusResponse;
import com.praveen.dto.UpdateStudentRoundStatusRequest;
import com.praveen.service.StudentRoundStatusService;

@RestController
@RequestMapping("/springApi/student-round-status")
public class StudentRoundStatusController {

    @Autowired
    private StudentRoundStatusService service;

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStudentRoundStatusRequest request) {
    	try {
    		service.updateStatus(id, request);
    		return ResponseEntity.ok("Updated The Status Successfully");
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Updating failed due to " + e.getMessage());
    	}
        
    }
}
