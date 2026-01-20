
package com.praveen.controller;

import com.praveen.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/springApi/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadStudents(@RequestParam Long driveId,
                                            @RequestParam("file") MultipartFile file) {
        try {
            studentService.uploadStudentsForDrive(driveId, file);
            return ResponseEntity.ok("Students uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Upload failed: " + e.getMessage());
        }
    }
}
