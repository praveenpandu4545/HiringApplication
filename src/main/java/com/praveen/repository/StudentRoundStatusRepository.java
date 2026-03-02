package com.praveen.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.StudentRoundStatus;

public interface StudentRoundStatusRepository extends JpaRepository<StudentRoundStatus, Long> {
	List<StudentRoundStatus> findByStudentDrive_Drive_IdAndRoundNumber(
	        Long driveId,
	        Integer roundNumber
	);
}