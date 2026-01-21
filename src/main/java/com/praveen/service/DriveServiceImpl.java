package com.praveen.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.praveen.repository.DriveRepository;
import com.praveen.dto.CreateDriveRequest;
import com.praveen.dto.DriveResponse;
import com.praveen.dto.RoundRequest;
import com.praveen.dto.RoundResponse;
import com.praveen.entities.*;
import java.util.*;

@Service
public class DriveServiceImpl implements DriveService {

    @Autowired
    private DriveRepository driveRepository;

    @Override
    public Drive createDrive(CreateDriveRequest request) {

        Drive drive = new Drive();
        drive.setCollegeName(request.getCollegeName());
        drive.setDriveName(request.getDriveName());
        drive.setNoOfRounds(request.getNoOfRounds());

        List<Round> roundEntities = new ArrayList<>();

        for (RoundRequest r : request.getRounds()) {
            Round round = new Round();
            round.setRoundNumber(r.getRoundNumber());
            round.setRoundName(r.getRoundName());
            round.setDrive(drive);
            roundEntities.add(round);
        }

        drive.setRounds(roundEntities);

        return driveRepository.save(drive);
    }

	@Override
	public List<DriveResponse> getAllDrives() {
		List<Drive> drives = driveRepository.findAll();
		List<DriveResponse> driveResponses = new ArrayList<>();
		for(Drive d : drives) {
			driveResponses.add(mapDriveToDriveResponse(d));
		}
		return driveResponses;
	}

	@Override
	public DriveResponse getDriveById(Long driveId) {
	    Drive drive = driveRepository.findById(driveId)
	            .orElseThrow(() -> new RuntimeException("Drive not found"));
	    return mapDriveToDriveResponse(drive);
	}
	
	
	// *****************************   HELPER FUNCTIONS   *****************************************
	
	private DriveResponse mapDriveToDriveResponse(Drive drive) {
		DriveResponse dr = new DriveResponse();
		dr.setCollegeName(drive.getCollegeName());
		dr.setDriveName(drive.getDriveName());
		dr.setId(drive.getId());
		dr.setNoOfRounds(drive.getNoOfRounds());
		List<RoundResponse> rr = new ArrayList<>();
		for (Round r : drive.getRounds()) {
			RoundResponse R = new RoundResponse();
			R.setId(r.getId());
			R.setRoundName(r.getRoundName());
			R.setRoundNumber(r.getRoundNumber());
			rr.add(R);
		}
		dr.setRounds(rr);
		return dr;
	}
}
