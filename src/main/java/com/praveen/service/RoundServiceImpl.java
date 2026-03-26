package com.praveen.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.dto.RoundResponse;
import com.praveen.entities.Round;
import com.praveen.repository.RoundRepository;

@Service // ✅ VERY IMPORTANT
public class RoundServiceImpl implements RoundService {

    @Autowired
    private RoundRepository roundRepository;

    @Override
    public List<RoundResponse> getRoundsById(Long driveId) {

        List<Round> rounds = roundRepository.findByDriveId(driveId);

        // convert Entity → DTO
        return rounds.stream()
                .map(r -> new RoundResponse(
                        r.getId(),
                        r.getRoundNumber(),
                        r.getRoundName()
                ))
                .collect(Collectors.toList());
    }
}