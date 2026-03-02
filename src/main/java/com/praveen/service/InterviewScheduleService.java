package com.praveen.service;

import java.time.LocalDateTime;
import java.util.List;

import com.praveen.dto.AutoScheduleRequest;
import com.praveen.dto.HRInterviewResponse;
import com.praveen.entities.InterviewSchedule;

public interface InterviewScheduleService {
    InterviewSchedule scheduleInterview(Long studentId,
                  Long panelMemberId,Long roundId,LocalDateTime startTime,LocalDateTime endTime);
    
    List<HRInterviewResponse> getInterviewsScheduledByHR();
    
    InterviewSchedule rescheduleInterview(Long interviewId,
    		Long panelMemberId, LocalDateTime startTime, LocalDateTime endTime
    );
    
    void autoSchedule(AutoScheduleRequest request);
}