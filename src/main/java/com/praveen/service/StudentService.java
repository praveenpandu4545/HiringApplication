package com.praveen.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.praveen.dto.StudentResponse;
import com.praveen.dto.StudentRoundStatusResponse;

public interface StudentService {
    void uploadStudentsForDrive(Long driveId, MultipartFile file);
	List<StudentResponse> getAllStudentsByDriveId(Long driveId);
	List<StudentRoundStatusResponse> getAllRoundsByStudentId(Long studentId);
}
