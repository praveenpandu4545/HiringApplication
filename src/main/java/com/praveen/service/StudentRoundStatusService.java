package com.praveen.service;
import org.springframework.stereotype.Service;
import com.praveen.dto.UpdateStudentRoundStatusRequest;

public interface StudentRoundStatusService {
	void updateStatus(Long id, UpdateStudentRoundStatusRequest request);
}
