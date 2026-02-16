package com.praveen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.College;

public interface CollegeRepository extends JpaRepository<College, Long>{

}
