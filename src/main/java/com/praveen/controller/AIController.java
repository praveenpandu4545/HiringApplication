package com.praveen.controller;

import com.praveen.dto.AIEligibilityRequest;
import com.praveen.entities.Drive;
import com.praveen.entities.Student;
import com.praveen.repository.DriveRepository;
import com.praveen.repository.StudentRepository;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/springApi/ai")
@RequiredArgsConstructor
public class AIController {

    private final StudentRepository studentRepository;
    private final DriveRepository driveRepository;
    private final WebClient mlWebClient;

    @PostMapping("/check/{driveId}")
    public ResponseEntity<?> checkEligibility(
            @PathVariable Long driveId,
            Authentication authentication) {

        try {

            String email = authentication.getName();

            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            Drive drive = driveRepository.findById(driveId)
                    .orElseThrow(() -> new RuntimeException("Drive not found"));

            byte[] resumeBytes = student.getResume();
            if (resumeBytes == null || resumeBytes.length == 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Resume not uploaded"
                        ));
            }

            String resumeText = extractTextFromPDF(resumeBytes);
            String driveRequirements = "";

            if (drive.getRequiredSkills() != null) {
                driveRequirements = String.join("\n", drive.getRequiredSkills());
            }

            AIEligibilityRequest request =
                    new AIEligibilityRequest(resumeText, driveRequirements);

            Object mlResponse = mlWebClient.post()
                    .uri("/check-eligibility")
                    .bodyValue(Map.of(
                            "resume_text", resumeText,
                            "drive_requirements", driveRequirements
                    ))
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .map(error -> new RuntimeException("FastAPI Error: " + error))
                    )
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", mlResponse
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "message", "AI check failed",
                            "error", e.getMessage()
                    ));
        }
    }
    
    @PostMapping("/checkATS")
    public ResponseEntity<?> checkATS(Authentication authentication) {

        try {
            String email = authentication.getName();

            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            byte[] resumeBytes = student.getResume();

            if (resumeBytes == null || resumeBytes.length == 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Resume not uploaded"
                ));
            }

            // 🔥 Extract text from PDF
            String resumeText = extractTextFromPDF(resumeBytes);
            String driveRequirements = "Suitable for any student";
            // 🔥 Send to ML service
            Object mlResponse = mlWebClient.post()
                    .uri("/check-ats")   // FastAPI endpoint
                    .bodyValue(Map.of(
                    	    "resume_text", resumeText,
                    	    "job_description", driveRequirements   // 🔥 USE REAL DATA
                    	))
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .map(error -> new RuntimeException("FastAPI Error: " + error))
                    )
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", mlResponse
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "ATS check failed",
                    "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/summarize-review/{driveId}")
    public ResponseEntity<?> summarizeReview(
            @PathVariable Long driveId,
            @RequestBody Map<String, String> body
    ) {
        try {

            String reviewText = body.get("review");

            Drive drive = driveRepository.findById(driveId)
                    .orElseThrow(() -> new RuntimeException("Drive not found"));

            String requirements = String.join("\n", drive.getRequiredSkills());

            // 🔥 Send to ML
            Object mlResponse = mlWebClient.post()
                    .uri("/summarize-review")
                    .bodyValue(Map.of(
                            "review", reviewText,
                            "requirements", requirements
                    ))
                    .retrieve()
                    .onStatus(status -> status.isError(), response ->
                        response.bodyToMono(String.class)
                                .map(error -> new RuntimeException("FastAPI Error: " + error))
                    )
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", mlResponse
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Summary failed",
                    "error", e.getMessage()
            ));
        }
    }

    // 🔥 Proper PDF Extraction	
    private String extractTextFromPDF(byte[] pdfBytes) throws Exception {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}