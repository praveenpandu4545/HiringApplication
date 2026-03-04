package com.praveen.controller;

import com.praveen.dto.AutoScheduleRequest;
import com.praveen.dto.HRInterviewResponse;
import com.praveen.dto.InterviewRescheduleRequest;
import com.praveen.dto.InterviewReviewRequest;
import com.praveen.dto.InterviewScheduleRequest;
import com.praveen.dto.PanelInterviewResponseDTO;
import com.praveen.entities.InterviewSchedule;
import com.praveen.repository.InterviewScheduleRepository;
import com.praveen.service.InterviewScheduleService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/springApi/interviews")
@RequiredArgsConstructor
public class InterviewScheduleController {

	@Autowired
    private final InterviewScheduleService interviewService;
    
	@Autowired
    private final InterviewScheduleRepository interviewScheduleRepository;

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
    
    @PutMapping("/{interviewId}/reschedule")
    public ResponseEntity<InterviewSchedule> rescheduleInterview(
            @PathVariable Long interviewId,
            @RequestBody InterviewRescheduleRequest request) {

        InterviewSchedule updated =
                interviewService.rescheduleInterview(
                        interviewId,
                        request.getPanelMemberId(),
                        request.getStartTime(),
                        request.getEndTime()
                );

        return ResponseEntity.ok(updated);
    }
    
    @PostMapping("/auto-schedule")
    public ResponseEntity<?> autoSchedule(
            @RequestBody AutoScheduleRequest request) {

        interviewService.autoSchedule(request);

        return ResponseEntity.ok("Auto scheduling completed successfully");
    }
    
    @GetMapping("/panel-interviews")
    public ResponseEntity<List<PanelInterviewResponseDTO>>
        getMyInterviews(
            @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(
        		interviewService.getPanelInterviews(token)
        );
    }
    
    @PutMapping("/review")
    public ResponseEntity<?> submitReview(@RequestBody InterviewReviewRequest request){

        InterviewSchedule schedule =
            interviewScheduleRepository
            .findById(request.getInterviewScheduleId())
            .orElseThrow(() -> new RuntimeException("Interview not found"));

        schedule.setReview(request.getReview());

        interviewScheduleRepository.save(schedule);

        return ResponseEntity.ok("Review saved successfully");
    }
}