package com.praveen.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.praveen.dto.CreateDriveRequest;
import com.praveen.entities.Drive;
import com.praveen.service.DriveService;

@RestController
@RequestMapping("/springApi/drive")
public class DriveController {

    @Autowired
    private DriveService driveService;

    @PostMapping("/CreateDrive")
    public ResponseEntity<Drive> createDrive(@RequestBody CreateDriveRequest request) {
        Drive savedDrive = driveService.createDrive(request);
        return ResponseEntity.ok(savedDrive);
    }
    
//    @PostMapping("/testing")
//    public ResponseEntity<?> upload(
//    	    @RequestParam("driveNumber") int driveNumber,
//    	    @RequestParam("file") MultipartFile file
//    	) {
//    	    System.out.println("drive: " + driveNumber);
//    	    System.out.println("file: " + file.getOriginalFilename());
//    	    return ResponseEntity.ok("Received");
//    	}
}
