package com.praveen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.College;

public interface CollegeRepository extends JpaRepository<College, Long>{
	Optional<College> findByCollegeName(String collegeName);
}
