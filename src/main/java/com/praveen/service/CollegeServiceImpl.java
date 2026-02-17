package com.praveen.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.entities.College;
import com.praveen.entities.CollegeDrive;
import com.praveen.entities.Drive;
import com.praveen.repository.CollegeRepository;

import jakarta.transaction.Transactional;

@Service
public class CollegeServiceImpl implements CollegeService{
	
	@Autowired
	private CollegeRepository collegeRepository;

	@Override
	public College registerNewCollege(String clgName) {
		try {
			Optional<College> existingCollege =
		            collegeRepository.findByCollegeName(clgName);

		    if (existingCollege.isPresent()) {
		        throw new RuntimeException("College already exists with name: " + clgName);
		    }
			College clg = new College();
			clg.setCollegeName(clgName);
			return collegeRepository.save(clg);
		}
		catch(Exception e) {
			throw new RuntimeException("College registration failed: " + e.getMessage());
		}
	}

	@Override
	public List<College> fetchAllColleges() {
		List<College> allColleges = collegeRepository.findAll();
		return allColleges;
	}

	@Override
	public List<Drive> getDrivesByName(String clgName) {

	    College college = collegeRepository
	            .findByCollegeName(clgName)
	            .orElseThrow(() -> new RuntimeException("College not found"));

	    List<Drive> drives = new ArrayList<>();

	    for (CollegeDrive cd : college.getCollegeDrives()) {
	        drives.add(cd.getDrive());
	    }

	    return drives;
	}

	
}
