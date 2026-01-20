package com.praveen.service;

import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
    void uploadStudentsForDrive(Long driveId, MultipartFile file);
}
