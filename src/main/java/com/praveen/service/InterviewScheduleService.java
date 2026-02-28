package com.praveen.service;

import java.time.LocalDateTime;

import com.praveen.entities.InterviewSchedule;

public interface InterviewScheduleService {
    InterviewSchedule scheduleInterview(Long studentId,
                                         Long panelMemberId,
                                         Long roundId,
                                         LocalDateTime startTime,
                                         LocalDateTime endTime);
}