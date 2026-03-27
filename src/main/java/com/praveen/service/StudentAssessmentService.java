package com.praveen.service;

import java.util.List;

import com.praveen.dto.StudentAssessmentResponse;
import com.praveen.entities.StudentAssessment;

public interface StudentAssessmentService {
	List<StudentAssessmentResponse> getAllStudentAssessments(String email);
}
