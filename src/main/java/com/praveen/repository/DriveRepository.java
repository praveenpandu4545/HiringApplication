package com.praveen.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.Drive;

public interface DriveRepository extends JpaRepository<Drive, Long> {
	List<Drive> findByCollegeName(String collegeName);
}
