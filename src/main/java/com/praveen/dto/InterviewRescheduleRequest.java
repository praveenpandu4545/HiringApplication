package com.praveen.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InterviewRescheduleRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long panelMemberId;
}