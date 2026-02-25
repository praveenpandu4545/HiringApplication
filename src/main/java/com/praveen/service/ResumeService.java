package com.praveen.service;

import org.springframework.web.multipart.MultipartFile;
import com.praveen.entities.Student;

public interface ResumeService {

    void uploadResume(MultipartFile file, String email) throws Exception;

    boolean resumeExists(String email);

    Student getStudentWithResume(String email);

    void deleteResume(String email);
}