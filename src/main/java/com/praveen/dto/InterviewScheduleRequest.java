package com.praveen.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterviewScheduleRequest {

    private Long studentId;
    private Long roundId;
    private Long panelMemberId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}