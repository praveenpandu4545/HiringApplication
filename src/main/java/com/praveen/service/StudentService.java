package com.praveen.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.praveen.dto.DriveResponse;
import com.praveen.dto.StudentResponse;
import com.praveen.dto.StudentRoundStatusResponse;
import com.praveen.entities.Drive;

public interface StudentService {
    void uploadStudentsForDrive(Long driveId, MultipartFile file);
	List<StudentResponse> getAllStudentsByDriveId(Long driveId);
	List<StudentRoundStatusResponse> getAllRoundsByStudentIdAndDriveId(Long studentId, Long driveid);
	List<DriveResponse> getDrivesByStudentId(Long studentId);
	
}
