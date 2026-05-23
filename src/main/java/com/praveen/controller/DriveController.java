package com.praveen.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.praveen.dto.CreateDriveRequest;
import com.praveen.dto.DriveResponse;
import com.praveen.entities.Drive;
import com.praveen.repository.DriveRepository;
import com.praveen.service.DriveService;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/springApi/drive")
public class DriveController {

    @Autowired
    private DriveService driveService;
    
    @Autowired
    private DriveRepository driveRepo;

    @PostMapping("/CreateDrive")
    public ResponseEntity<?> createDrive(@RequestBody CreateDriveRequest request) {
        try {
        	Drive savedDrive = driveService.createDrive(request);
    		return ResponseEntity.ok(savedDrive);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Creating drive failed due to " + e.getMessage());
    	}
    }
    
    @GetMapping("/getAll")
    public ResponseEntity<?> getAllDrives(){
    	try {
    		List<DriveResponse> drives = driveService.getAllDrives();
    		return ResponseEntity.ok(drives);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Fetching all drives failed due to " + e.getMessage());
    	}
    }
 
    @GetMapping("/{driveId}")
    public ResponseEntity<?> getDrive(@PathVariable Long driveId){
    	try {
    		DriveResponse dr= driveService.getDriveById(driveId);
    		return ResponseEntity.ok(dr);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Fetching all drives failed due to " + e.getMessage());
    	}
    }
    
    @GetMapping("/student")
    public ResponseEntity<?> getDrivesForStudent(Authentication authentication) {
        try {
            String email = authentication.getName();
            return ResponseEntity.ok(driveService.getDrivesForStudent(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Failed to fetch drives: " + e.getMessage());
        }
    }
    
    @PatchMapping("/rename/{id}")
    public ResponseEntity<?> renameDrive(@PathVariable Long id, @RequestParam String newName){
    	Optional<Drive> drive = driveRepo.findById(id);
    	if(drive.isPresent()) {
    		Drive d = drive.get();
    		d.setDriveName(newName);
    		driveRepo.save(d);
    		return ResponseEntity.ok("Drive name changed to " + newName);
    	}
    	else return ResponseEntity.badRequest().body("Drive not found");
    }
}
