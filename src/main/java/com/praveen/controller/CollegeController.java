package com.praveen.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.praveen.entities.College;
import com.praveen.entities.Drive;
import com.praveen.service.CollegeService;

@RestController
@RequestMapping("/springApi/college")
public class CollegeController {
	
	@Autowired
	private CollegeService collegeService;
	
	@PostMapping("/registerNewCollege")
	public ResponseEntity<?> registerNewCollege(@RequestBody College clg){
		try {
			College response = collegeService.registerNewCollege(clg.getCollegeName());
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		}
		catch(Exception e) {
			return ResponseEntity.badRequest()
	                .body("College registration failed: " + e.getMessage());
		}
	}
	
	@GetMapping("/getAll")
	public ResponseEntity<?> getAllColleges(){
		try {
			List<College> allColleges = collegeService.fetchAllColleges();
			List<String> clgs = new ArrayList<>();
			for(College clg : allColleges) {
				if(!clg.isDeleted()) {
					clgs.add(clg.getCollegeName());
				}
			}
			return ResponseEntity.ok(clgs);
		}
		catch(Exception e) {
			return ResponseEntity.badRequest()
	                .body("Fetching Colleges Failed Due To " + e.getMessage());
		}
	}
	
	@GetMapping("/getDrivesByClgName/{clgName}")
	public ResponseEntity<?> getDrivesByName(@PathVariable String clgName){
		try {
			List<Drive> list = collegeService.getDrivesByName(clgName);
			return ResponseEntity.ok(list);
		}
		catch(Exception e) {
			return ResponseEntity.badRequest()
	                .body("Fetching drives Failed Due To " + e.getMessage());
		}
	}
}
