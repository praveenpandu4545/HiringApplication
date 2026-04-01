package com.praveen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.*;

public interface StudentResultRepository extends JpaRepository<StudentResult, Long> {

    int countByStudentAndAssessment(Student student, Assessment assessment);
    
    List<StudentResult> findByAssessment_Id(Long assessmentId);
}