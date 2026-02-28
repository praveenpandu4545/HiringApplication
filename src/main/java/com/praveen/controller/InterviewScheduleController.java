package com.praveen.controller;

import com.praveen.dto.HRInterviewResponse;
import com.praveen.dto.InterviewScheduleRequest;
import com.praveen.entities.InterviewSchedule;
import com.praveen.service.InterviewScheduleService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/springApi/interviews")
@RequiredArgsConstructor
public class InterviewScheduleController {

    private final InterviewScheduleService interviewService;

    @PostMapping("/schedule")
    public ResponseEntity<InterviewSchedule> scheduleInterview(
            @RequestBody InterviewScheduleRequest request) {

        InterviewSchedule schedule = interviewService.scheduleInterview(
                request.getStudentId(),
                request.getPanelMemberId(),
                request.getRoundId(),
                request.getStartTime(),
                request.getEndTime()
        );

        return ResponseEntity.ok(schedule);
    }
    
    @GetMapping("/hr")
    public ResponseEntity<?> getHRInterviews() {

        List<HRInterviewResponse> interviews =
                interviewService.getInterviewsScheduledByHR();

        return ResponseEntity.ok(interviews);
    }
}