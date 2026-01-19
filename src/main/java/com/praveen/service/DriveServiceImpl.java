package com.praveen.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.praveen.repository.DriveRepository;
import com.praveen.dto.CreateDriveRequest;
import com.praveen.dto.RoundRequest;
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
}
