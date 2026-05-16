
package com.praveen.controller;

import com.praveen.dto.ChangePasswordRequest;
import com.praveen.dto.DriveResponse;
import com.praveen.dto.IdDTO;
import com.praveen.dto.StudentResponse;
import com.praveen.dto.StudentRoundStatusResponse;
import com.praveen.entities.Drive;
import com.praveen.entities.Student;
import com.praveen.repository.StudentRepository;
import com.praveen.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@RestController
@RequestMapping("/springApi/student")
public class StudentController {

    @Autowired
    private StudentService studentService;
    
    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadStudents(@RequestParam Long driveId,
                                            @RequestParam("file") MultipartFile file) {
        try {
            studentService.uploadStudentsForDrive(driveId, file);
            return ResponseEntity.ok("Students uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Upload failed: " + e.getMessage());
        }
    }
    
    // TO GET ALL THE STUDENTS IN A DRIVE
    @GetMapping("/getAll/{driveId}")
    public ResponseEntity<?> getAllStudentsByDrive(@PathVariable Long driveId){
    	
    	try {
    		List<StudentResponse> studentResponses = studentService.getAllStudentsByDriveId(driveId);
    		return ResponseEntity.ok(studentResponses);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Unable to fetch students due to " + e.getMessage());
    	}
    }
    
    // TO GET ALL THE ROUNDS OF A STUDENT USING DRIVE ID AS WELL
    
    @GetMapping("/getAllRounds/{studentId}/driveId/{driveId}")
    public ResponseEntity<?> getAllRoundsByStudent(@PathVariable Long studentId, @PathVariable Long driveId){
    	try {
    		List<StudentRoundStatusResponse> srsr = studentService.getAllRoundsByStudentIdAndDriveId(studentId,driveId);
    		return ResponseEntity.ok(srsr);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Unable to fetch students due to " + e.getMessage());
    	}
    } 
    
    // TO GET A DRIVE USING STUDENT_ID
    
    @GetMapping("/getDrives/{studentId}")
    public ResponseEntity<?> getDrivesByStudentId(@PathVariable Long studentId){
    	try {
    		List<DriveResponse> drives = studentService.getDrivesByStudentId(studentId);
    		return ResponseEntity.ok(drives);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Fetching Drives Failed Due to " + e.getMessage());
    	}
    }
    
    @GetMapping("/{studentEmail}")
    public ResponseEntity<?> getStudent(@PathVariable String studentEmail){
    	try {
    		StudentResponse student = studentService.getStudent(studentEmail);
    		return ResponseEntity.ok(student);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Fetching Student Failed Due to " + e.getMessage());
    	}
    }
    
    
    // TO FETCH ALL ROUNDS OF A STUDENT IN HIS LOGIN 
    
    @GetMapping("/getAllRounds/{driveId}")
    public ResponseEntity<?> getAllRoundsByStudent(
            @PathVariable Long driveId,
            Authentication authentication) {

        try {

            // 🔐 Extract email from JWT
            String email = authentication.getName();

            // 🔎 Find student using email
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // 📌 Call service using studentId
            List<StudentRoundStatusResponse> rounds =
                    studentService.getAllRoundsByStudentIdAndDriveId(
                            student.getId(), driveId);

            return ResponseEntity.ok(rounds);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body("Unable to fetch rounds due to " + e.getMessage());
        }
    }
    
    @GetMapping("/get-user-id/{studentId}")
    public ResponseEntity<?> getUserId( @PathVariable Long studentId){
    	Optional<Student> optionalStudent = studentRepository.findById(studentId);
    	if(optionalStudent.isPresent()) {
    		Student student = optionalStudent.get();
    		Long userId = student.getUser().getId();
    		return ResponseEntity.ok(userId);
    	}
    	return ResponseEntity
                .badRequest()
                .body("Student not found");
    }

    
}
