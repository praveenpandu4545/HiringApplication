package com.praveen.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.entities.College;
import com.praveen.repository.CollegeRepository;

@Service
public class CollegeServiceImpl implements CollegeService{
	
	@Autowired
	private CollegeRepository collegeRepository;

	@Override
	public College registerNewCollege(String clgName) {
		try {
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
	
}
