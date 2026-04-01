package com.praveen.service;

import com.praveen.dto.AssessmentRequest;
import com.praveen.dto.AssessmentResponse;
import com.praveen.dto.AssessmentSubmissionRequest;
import com.praveen.dto.AssessmentSubmissionResponse;
import com.praveen.dto.StudentResultResponse;
import com.praveen.entities.Assessment;
import com.praveen.entities.StudentResult;

import java.util.List;

public interface AssessmentService {

    Assessment saveAssessment(AssessmentRequest request);
    List<AssessmentResponse> getAllAssessments();
    AssessmentSubmissionResponse submitAssessment(
            AssessmentSubmissionRequest request,
            String email
    );
    List<StudentResultResponse> getResultsByAssessment(Long assessmentId);
}