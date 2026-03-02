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
            String driveRequirements = String.join("\n", drive.getRequiredSkills());

            AIEligibilityRequest request =
                    new AIEligibilityRequest(resumeText, driveRequirements);

            Object mlResponse = mlWebClient.post()
                    .uri("/check-eligibility")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Object.class)
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
    public String checkATS() {
        return "Your ATS is perfect";
    }

    // 🔥 Proper PDF Extraction	
    private String extractTextFromPDF(byte[] pdfBytes) throws Exception {
        try (PDDocument document = PDDocument.load(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}