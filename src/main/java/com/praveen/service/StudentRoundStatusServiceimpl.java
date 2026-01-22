package com.praveen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.dto.UpdateStudentRoundStatusRequest;
import com.praveen.entities.StudentRoundStatus;
import com.praveen.repository.StudentRoundStatusRepository;

@Service
public class StudentRoundStatusServiceimpl implements StudentRoundStatusService{
	
	@Autowired
	private StudentRoundStatusRepository repo;

	@Override
	public void updateStatus(Long id, UpdateStudentRoundStatusRequest request) {
		StudentRoundStatus entity = repo.findById(id)
			        .orElseThrow(() -> new RuntimeException("Student round status not found"));
		entity.setStatus(request.getStatus());
		repo.save(entity);
	}
}
