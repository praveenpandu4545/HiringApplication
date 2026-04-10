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
    
    // 🔥 START CALL (Panel)
    @PostMapping("/start")
    public Call startCall(@RequestBody Call request, Authentication auth) {

        String username = auth.getName();
        Long callerId = userService.getUserIdFromUsername(username);

        request.setCallerId(callerId);
        request.setStatus("CALLING");
        request.setChannelName("interview_" + request.getInterviewId());

        return callRepository.save(request);
    }

    // 🔥 INCOMING CALL (Student)
    @GetMapping("/incoming-call")
    public ResponseEntity<?> getIncomingCall(Authentication auth) {

        String username = auth.getName();
        Long studentId = userService.getUserIdFromUsername(username);
        Optional<Call> call = callRepository
                .findByReceiverIdAndStatus(studentId, "CALLING");

        if (call.isPresent()) {
            return ResponseEntity.ok(call.get());
        } else {
            return ResponseEntity.ok(null); // important
        }
    }

    // 🔥 ACCEPT CALL
    @PostMapping("/accept/{id}")
    public void acceptCall(@PathVariable Long id) {
        Call call = callRepository.findById(id).orElseThrow();
        call.setStatus("ACCEPTED");
        callRepository.save(call);
    }

    // 🔥 REJECT CALL
    @PostMapping("/reject/{id}")
    public void rejectCall(@PathVariable Long id) {
        Call call = callRepository.findById(id).orElseThrow();
        call.setStatus("REJECTED");
        callRepository.save(call);
    }
}