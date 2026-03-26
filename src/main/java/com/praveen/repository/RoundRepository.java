package com.praveen.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.Round;

public interface RoundRepository extends JpaRepository<Round, Long> {
	List<Round> findByDriveId(Long driveId);
}
