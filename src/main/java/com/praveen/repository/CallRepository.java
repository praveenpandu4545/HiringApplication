package com.praveen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.Call;

public interface CallRepository extends JpaRepository<Call, Long> {
	Optional<Call> findByReceiverIdAndStatus(Long receiverId, String status);
}