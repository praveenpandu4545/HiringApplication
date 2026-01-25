package com.praveen.repository;

import com.praveen.entities.Student;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
	Optional<Student> findByStudentId(String studentId);
}
