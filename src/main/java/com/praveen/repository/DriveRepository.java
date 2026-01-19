package com.praveen.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.Drive;

public interface DriveRepository extends JpaRepository<Drive, Long> {
}
