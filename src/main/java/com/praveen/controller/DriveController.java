package com.praveen.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;

import com.praveen.dto.CreateDriveRequest;
import com.praveen.dto.DriveResponse;
import com.praveen.entities.Drive;
import com.praveen.service.DriveService;

@RestController
@RequestMapping("/springApi/drive")
public class DriveController {

    @Autowired
    private DriveService driveService;

    @PostMapping("/CreateDrive")
    public ResponseEntity<?> createDrive(@RequestBody CreateDriveRequest request) {
        try {
        	Drive savedDrive = driveService.createDrive(request);
    		return ResponseEntity.ok(savedDrive);
    	}
    	catch(Exception e) {
    		return ResponseEntity.status(400).body("Fetching all drives failed due to " + e.getMessage());
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
}
