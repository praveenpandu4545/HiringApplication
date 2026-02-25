package com.praveen.service;

import com.praveen.entities.Student;
import com.praveen.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final StudentRepository studentRepository;

    @Override
    public void uploadResume(MultipartFile file, String email) throws Exception {

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Optional: Allow only PDF
        if (!file.getContentType().equals("application/pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        student.setResume(file.getBytes());
        student.setResumeName(file.getOriginalFilename());
        student.setResumeType(file.getContentType());

        studentRepository.save(student);
    }

    @Override
    public boolean resumeExists(String email) {

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return student.getResume() != null;
    }

    @Override
    public Student getStudentWithResume(String email) {

        return studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }

    @Override
    public void deleteResume(String email) {

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (student.getResume() == null) {
            throw new RuntimeException("No resume to delete");
        }

        student.setResume(null);
        student.setResumeName(null);
        student.setResumeType(null);

        studentRepository.save(student);
    }
}