package com.praveen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.praveen.dto.RoundResponse;
import com.praveen.service.RoundService;

@RestController
@RequestMapping("/springApi/round")
public class RoundController {

    @Autowired
    private RoundService roundService;

    @GetMapping("/getRoundsByDriveId/{driveId}")
    public List<RoundResponse> getRoundsByDriveId(@PathVariable Long driveId) {
        return roundService.getRoundsById(driveId);
    }
}