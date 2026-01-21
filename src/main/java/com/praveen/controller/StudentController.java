
package com.praveen.controller;

import com.praveen.dto.StudentResponse;
import com.praveen.dto.StudentRoundStatusResponse;
import com.praveen.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@RestController
@RequestMapping("/springApi/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

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
    	List<StudentResponse> studentResponses = studentService.getAllStudentsByDriveId(driveId);
    	try {
    		return ResponseEntity.ok(studentResponses);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Unable to fetch students due to " + e.getMessage());
    	}
    }
    
    // TO GET ALL THE ROUNDS OF A STUDENT 
    
    @GetMapping("/getAllRounds/{studentId}")
    public ResponseEntity<?> getAllRoundsByStudent(@PathVariable Long studentId){
    	List<StudentRoundStatusResponse> srsr = studentService.getAllRoundsByStudentId(studentId);
    	try {
    		return ResponseEntity.ok(srsr);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Unable to fetch students due to " + e.getMessage());
    	}
    } 
}
