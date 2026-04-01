package com.praveen.controller;

import com.praveen.dto.AssessmentRequest;
import com.praveen.dto.AssessmentResponse;
import com.praveen.dto.AssessmentSubmissionRequest;
import com.praveen.dto.AssessmentSubmissionResponse;
import com.praveen.dto.StudentAssessmentResponse;
import com.praveen.dto.StudentResultResponse;
import com.praveen.entities.Assessment;
import com.praveen.entities.StudentResult;
import com.praveen.repository.AssessmentRepository;
import com.praveen.security.JwtUtil;
import com.praveen.service.AssessmentService;
import com.praveen.service.StudentAssessmentService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/springApi/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;
    
    public StudentAssessmentService studentAssessmentService;
    
    private final AssessmentRepository assessmentRepository;
    
    @Autowired
    public JwtUtil jwtUtil;
    

    public AssessmentController(AssessmentService assessmentService,
    		StudentAssessmentService studentAssessmentService,
    		AssessmentRepository assessmentRepository) {
        this.assessmentService = assessmentService;
        this.studentAssessmentService = studentAssessmentService;
        this.assessmentRepository = assessmentRepository;
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
    
    @PostMapping("/submit")
    public ResponseEntity<AssessmentSubmissionResponse> submitAssessment(
            @RequestBody AssessmentSubmissionRequest request,
            Authentication authentication) {

        // 🔐 Extract email from JWT
        String email = authentication.getName();

        AssessmentSubmissionResponse response =
                assessmentService.submitAssessment(request, email);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/results/{assessmentId}")
    public ResponseEntity<List<StudentResultResponse>> getResultsByAssessment(
            @PathVariable Long assessmentId) {

        List<StudentResultResponse> results =
        		assessmentService.getResultsByAssessment(assessmentId);

        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/{id}")
    public Assessment getAssessmentById(@PathVariable Long id) {
    	Assessment assessment = assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // 🔥 LOAD QUESTIONS
        assessment.getAssessmentQuestions().forEach(aq -> {
            aq.getQuestion().getQuestionText();
            aq.getQuestion().getOptions();
        });

        return assessment;
    }
    
}