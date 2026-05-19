package com.praveen.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.praveen.entities.Drive;
import com.praveen.entities.Round;
import com.praveen.entities.Student;
import com.praveen.entities.StudentDrive;
import com.praveen.entities.StudentRoundStatus;
import com.praveen.repository.DriveRepository;
import com.praveen.repository.StudentRepository;
import com.praveen.repository.StudentRoundStatusRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/springApi/myapplication")
public class MyApplicationController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DriveRepository driveRepository;

    @Autowired
    private StudentRoundStatusRepository studentRoundStatusRepository;

    @GetMapping("/isRegistered/{driveId}")
    public ResponseEntity<?> isRegistered(
            @PathVariable Long driveId,
            Authentication authentication) {

        String email = authentication.getName();

        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        boolean registered = student.getStudentDrives()
                .stream()
                .anyMatch(sd -> sd.getDrive().getId().equals(driveId));

        return ResponseEntity.ok(Map.of("registered", registered));
    }
    
    @Transactional
    @PostMapping("/register/{driveId}")
    public ResponseEntity<?> registerStudentForDrive(
            @PathVariable Long driveId,
            Authentication authentication) {

        try {

            // 🔐 Extract student email from JWT
            String email = authentication.getName();

            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            Drive drive = driveRepository.findById(driveId)
                    .orElseThrow(() -> new RuntimeException("Drive not found"));

            // ✅ Check already registered
            boolean alreadyRegistered = student.getStudentDrives()
                    .stream()
                    .anyMatch(sd -> sd.getDrive().getId().equals(driveId));

            if (alreadyRegistered) {
                return ResponseEntity.ok("Already Registered");
            }

            // 🔥 Create StudentDrive
            StudentDrive sd = new StudentDrive();
            sd.setStudent(student);
            sd.setDrive(drive);
            sd.setFinalStatus("IN PROGRESS");

            // 🔥 Create round statuses
            for (Round r : drive.getRounds()) {

                StudentRoundStatus srs = new StudentRoundStatus();
                srs.setRoundName(r.getRoundName());
                srs.setRoundNumber(r.getRoundNumber());
                srs.setStatus("PENDING");
                srs.setStudentDrive(sd);
                srs.setCanSchedule(r.getCanSchedule());
                sd.getStudentRoundStatuses().add(srs);
            }

            // 🔗 Maintain relationship
            student.getStudentDrives().add(sd);
            drive.getStudentDrives().add(sd);

            // 💾 Save student (cascade will save everything)
            studentRepository.save(student);

            return ResponseEntity.ok("Registered Successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Registration failed: " + e.getMessage());
        }
    }
    
    
}