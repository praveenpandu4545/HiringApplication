package com.praveen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.StudentAssessment;

public interface StudentAssessmentRepository extends JpaRepository<StudentAssessment, Long>{

}

