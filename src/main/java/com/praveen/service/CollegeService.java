package com.praveen.service;

import java.util.List;

import com.praveen.entities.College;

public interface CollegeService {

	public College registerNewCollege(String clgName);
	public List<College> fetchAllColleges();

}
