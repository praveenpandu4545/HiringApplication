package com.praveen.service;
import com.praveen.dto.CreateDriveRequest;
import com.praveen.entities.Drive;
import com.praveen.dto.DriveResponse;
import java.util.*;

public interface DriveService {
    Drive createDrive(CreateDriveRequest request);
    List<DriveResponse> getAllDrives();
	DriveResponse getDriveById(Long driveId);
}
