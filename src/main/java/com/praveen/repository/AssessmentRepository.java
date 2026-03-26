package com.praveen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.Assessment;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
}