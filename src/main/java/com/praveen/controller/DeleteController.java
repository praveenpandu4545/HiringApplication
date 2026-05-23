package com.praveen.controller;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.praveen.entities.College;
import com.praveen.entities.Drive;
import com.praveen.entities.NoticeBoard;
import com.praveen.repository.AssessmentRepository;
import com.praveen.repository.CollegeRepository;
import com.praveen.repository.DriveRepository;
import com.praveen.repository.NoticeBoardRepository;
import com.praveen.entities.Assessment;

@RestController
@RequestMapping("/springApi/delete")
public class DeleteController {
	
	@Autowired
	private CollegeRepository clgRepo;
	
	@Autowired
	private DriveRepository driveRepo;
	
	@Autowired
	private NoticeBoardRepository noticeRepo;
	
	@Autowired
	private AssessmentRepository assessmentRepository;
	
	@DeleteMapping("/college")
	public ResponseEntity<?> deleteCollegeById(@RequestParam  String clgName){
		College college = clgRepo.findByCollegeName(clgName).orElseThrow(
				()-> new RuntimeException("College not found"));
		college.setDeleted(true);
		clgRepo.save(college);
		return ResponseEntity.ok("College deleted successfully");
	}

	@DeleteMapping("/drive/{id}")
	public ResponseEntity<?> deleteDriveById(@PathVariable Long id){
		Drive drive = driveRepo.findById(id).orElseThrow(
				()-> new RuntimeException("Drive not found"));
		drive.setDeleted(true);
		driveRepo.save(drive);
		return ResponseEntity.ok("Drive deleted successfully");
	}
	
	@DeleteMapping("/notice/{id}")
	public ResponseEntity<?> deleteNoticeById(@PathVariable Long id){
		NoticeBoard notice = noticeRepo.findById(id).orElseThrow(
				()-> new RuntimeException("Notice not found"));
		notice.setDeleted(true);
		noticeRepo.save(notice);
		return ResponseEntity.ok("Notice deleted successfully");
	}
	
	@DeleteMapping("/assessment/{id}")
	public ResponseEntity<?> deleteAssessmentById(@PathVariable Long id){
		Assessment assessment = assessmentRepository.findById(id).orElseThrow(
				()-> new RuntimeException("Assessment not found"));
		assessment.setDeleted(true);
		assessmentRepository.save(assessment);
		return ResponseEntity.ok("Assessment deleted successfully");
	}
	
	@GetMapping("/get-role")
	public ResponseEntity<?> getRole(
	        Authentication authentication) {

	    return ResponseEntity.ok(
	            authentication
	                    .getAuthorities()
	                    .stream()
	                    .findFirst()
	                    .get()
	                    .getAuthority()
	    );
	}
	
}
