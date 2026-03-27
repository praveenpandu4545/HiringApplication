package com.praveen.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.dto.StudentAssessmentResponse;
import com.praveen.entities.*;
import com.praveen.repository.StudentRepository;

@Service
public class StudentAssessmentServiceImpl implements StudentAssessmentService{
	
	@Autowired
	public StudentRepository studentRepository;

	@Override
	public List<StudentAssessmentResponse> getAllStudentAssessments(String email) {
		Student student = studentRepository.findByUser_Email(email)
		        .orElseThrow(() -> new RuntimeException("Student Not Found"));
		List<StudentAssessmentResponse> response = new ArrayList<>();
		
		for(StudentAssessment sa : student.getStudentAssessments()) {
			StudentAssessmentResponse sar = new StudentAssessmentResponse();
			sar.setId(sa.getId());
			sar.setAssessmentName(sa.getAssessment().getTitle());
			response.add(sar);
		}
		return response;
	}

}
