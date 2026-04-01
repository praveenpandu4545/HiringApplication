package com.praveen.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.dto.StudentAssessmentResponse;
import com.praveen.entities.*;
import com.praveen.repository.StudentRepository;

import jakarta.transaction.Transactional;

@Service
public class StudentAssessmentServiceImpl implements StudentAssessmentService {

    @Autowired
    public StudentRepository studentRepository;

    @Override
    @Transactional
    public List<StudentAssessmentResponse> getAllStudentAssessments(String email) {

        Student student = studentRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Student Not Found"));

        List<StudentAssessmentResponse> response = new ArrayList<>();

        for (StudentAssessment sa : student.getStudentAssessments()) {

            Assessment a = sa.getAssessment();

            StudentAssessmentResponse dto = StudentAssessmentResponse.builder()
                    .id(sa.getId())
                    .assessmentId(a.getId())
                    .title(a.getTitle())
                    .description(a.getDescription())
                    .startTime(a.getStartTime())
                    .endTime(a.getEndTime())
                    .duration(a.getDuration())
                    .active(a.isActive())
                    .status(sa.getStatus())
                    .build();

            response.add(dto);
        }

        return response;
    }
}