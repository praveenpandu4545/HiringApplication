package com.praveen.service;

import java.util.List;

import com.praveen.entities.College;
import com.praveen.entities.Drive;

public interface CollegeService {

	public College registerNewCollege(String clgName);
	public List<College> fetchAllColleges();
	public List<Drive> getDrivesByName(String clgName);

}
