package com.praveen.controller;

import com.praveen.entities.Student;
import com.praveen.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/springApi/student/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // ===== Upload / Replace =====
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            resumeService.uploadResume(file, email);
            return ResponseEntity.ok("Resume uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Upload failed: " + e.getMessage());
        }
    }

    // ===== Check Resume Exists =====
    @GetMapping("/info")
    public ResponseEntity<?> resumeInfo(Authentication authentication) {

        String email = authentication.getName();
        boolean exists = resumeService.resumeExists(email);

        return ResponseEntity.ok(exists);
    }

    // ===== Download =====
    @GetMapping("/download")
    public ResponseEntity<?> downloadResume(Authentication authentication) {

        String email = authentication.getName();
        Student student = resumeService.getStudentWithResume(email);

        if (student.getResume() == null) {
            return ResponseEntity.badRequest().body("No resume uploaded");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + student.getResumeName() + "\"")
                .contentType(MediaType.parseMediaType(student.getResumeType()))
                .body(student.getResume());
    }

    // ===== Delete =====
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteResume(Authentication authentication) {

        try {
            String email = authentication.getName();
            resumeService.deleteResume(email);
            return ResponseEntity.ok("Resume deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Delete failed: " + e.getMessage());
        }
    }
}