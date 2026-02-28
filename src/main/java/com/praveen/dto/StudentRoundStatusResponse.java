package com.praveen.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudentRoundStatusResponse {

    private Long id;
    private String roundName;
    private int roundNumber;
    private String status;

    // 🔥 NEW FIELDS
    private boolean interviewScheduled;
    private LocalDateTime interviewStartTime;
    private LocalDateTime interviewEndTime;
    private String panelName;
}