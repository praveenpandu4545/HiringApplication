package com.praveen.service;

import com.praveen.dto.AssessmentRequest;
import com.praveen.dto.AssessmentResponse;
import com.praveen.entities.Assessment;

import java.util.List;

public interface AssessmentService {

    Assessment saveAssessment(AssessmentRequest request);
    List<AssessmentResponse> getAllAssessments();
}