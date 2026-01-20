package com.praveen.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.StudentRoundStatus;

public interface StudentRoundStatusRepository extends JpaRepository<StudentRoundStatus, Long> {}