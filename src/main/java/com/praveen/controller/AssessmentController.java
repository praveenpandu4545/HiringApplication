package com.praveen.controller;

import com.praveen.dto.AssessmentRequest;
import com.praveen.dto.AssessmentResponse;
import com.praveen.entities.Assessment;
import com.praveen.service.AssessmentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/springApi/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveAssessment(@RequestBody AssessmentRequest request) {
        try {
            Assessment saved = assessmentService.saveAssessment(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


    @GetMapping("/getAll")
    public ResponseEntity<?> getAllAssessments() {
        try {
        	List<AssessmentResponse> list = assessmentService.getAllAssessments();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}