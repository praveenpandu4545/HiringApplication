package com.praveen.controller;

import com.praveen.dto.AssessmentRequest;
import com.praveen.dto.AssessmentResponse;
import com.praveen.dto.StudentAssessmentResponse;
import com.praveen.entities.Assessment;
import com.praveen.security.JwtUtil;
import com.praveen.service.AssessmentService;
import com.praveen.service.StudentAssessmentService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/springApi/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;
    
    public StudentAssessmentService studentAssessmentService;
    
    @Autowired
    public JwtUtil jwtUtil;
    

    public AssessmentController(AssessmentService assessmentService,
    		StudentAssessmentService studentAssessmentService) {
        this.assessmentService = assessmentService;
        this.studentAssessmentService = studentAssessmentService;
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
    
    @GetMapping("/getStudentAssessments")
    public List<StudentAssessmentResponse> getMyAssessments(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        return studentAssessmentService.getAllStudentAssessments(email);
    }
}