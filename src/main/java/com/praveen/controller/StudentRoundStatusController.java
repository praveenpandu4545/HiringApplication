package com.praveen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.praveen.dto.BulkUpdateResponse;
import com.praveen.dto.UpdateStudentRoundStatusRequest;
import com.praveen.service.StudentRoundStatusService;

@RestController
@RequestMapping("/springApi/student-round-status")
public class StudentRoundStatusController {

    @Autowired
    private StudentRoundStatusService service;

    // 🔹 Manual Update (Already Existing)
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStudentRoundStatusRequest request) {

        try {
            service.updateStatus(id, request);
            return ResponseEntity.ok("Updated The Status Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body("Updating failed due to " + e.getMessage());
        }
    }

    // 🔹 Smart Bulk Excel Update
    @PostMapping("/bulk-update")
    public ResponseEntity<?> bulkUpdateStatus(
            @RequestParam("driveId") Long driveId,
            @RequestParam("file") MultipartFile file) {

        try {

            BulkUpdateResponse response =
                    service.bulkUpdateStatus(file, driveId);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.status(400)
                    .body("Bulk update failed: " + e.getMessage());
        }
    }
}