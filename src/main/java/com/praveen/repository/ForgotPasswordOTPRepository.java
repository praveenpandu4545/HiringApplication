package com.praveen.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.praveen.entities.ForgotPasswordOTP;

public interface ForgotPasswordOTPRepository extends JpaRepository<ForgotPasswordOTP, Long>{
	
	Optional<ForgotPasswordOTP> findByEmail(String email);
	
}
