package com.praveen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.praveen.entities.Assessment;
import com.praveen.entities.Student;
import com.praveen.entities.StudentAssessment;

public interface StudentAssessmentRepository extends JpaRepository<StudentAssessment, Long>{

    Optional<StudentAssessment> findByStudentAndAssessment(Student student, Assessment assessment);

}