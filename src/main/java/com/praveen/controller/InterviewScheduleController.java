package com.praveen.controller;

import com.praveen.dto.AutoScheduleRequest;
import com.praveen.dto.HRInterviewResponse;
import com.praveen.dto.InterviewRescheduleRequest;
import com.praveen.dto.InterviewReviewRequest;
import com.praveen.dto.InterviewScheduleRequest;
import com.praveen.dto.PanelInterviewResponseDTO;
import com.praveen.entities.Call;
import com.praveen.entities.InterviewSchedule;
import com.praveen.repository.CallRepository;
import com.praveen.repository.InterviewScheduleRepository;
import com.praveen.service.InterviewScheduleService;
import com.praveen.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/springApi/interviews")
@RequiredArgsConstructor
public class InterviewScheduleController {

	@Autowired
    private final InterviewScheduleService interviewService;
	
	@Autowired
	private final CallRepository callRepository;
    
	@Autowired
    private final InterviewScheduleRepository interviewScheduleRepository;
	
	@Autowired
	private final UserService userService;

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
    
    @PostMapping("/start")
    public ResponseEntity<?> startCall(@RequestBody Call request, Authentication auth) {

        if (auth == null || auth.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        if (request.getReceiverId() == null) {
            return ResponseEntity.badRequest().body("ReceiverId is required");
        }

        String username = auth.getName();
        Long callerId = userService.getUserIdFromUsername(username);

        request.setCallerId(callerId);
        request.setStatus("CALLING");
        request.setChannelName("interview_" + request.getInterviewId());

        Call saved = callRepository.save(request);

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/incoming-call")
    public ResponseEntity<?> getIncomingCall(Authentication auth) {

        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            String username = auth.getName();
            Long studentId = userService.getUserIdFromUsername(username);

            if (studentId == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            Call call = callRepository
            	    .findTopByReceiverIdAndStatusInOrderByIdDesc(
            	        studentId,
            	        List.of("CALLING", "ACCEPTED")
            	    );

            return ResponseEntity.ok(call); // null is fine

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error fetching call");
        }
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptCall(@PathVariable Long id) {
        Call call = callRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Call not found"));

        call.setStatus("ACCEPTED");
        callRepository.save(call);

        return ResponseEntity.ok(call);
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectCall(@PathVariable Long id) {
        Call call = callRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Call not found"));

        call.setStatus("REJECTED");
        callRepository.save(call);

        return ResponseEntity.ok(call);
    }
    
    @PostMapping("/end-call/{id}")
    public ResponseEntity<?> endCall(@PathVariable Long id) {

        Call call = callRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Call not found"));

        call.setStatus("COMPLETED");
        callRepository.save(call);

        return ResponseEntity.ok("Call ended");
    }
}