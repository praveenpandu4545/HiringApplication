package com.praveen.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HRInterviewResponse {

    private String collegeName;
    private String driveName;
    private String studentName;
    private String studentEmail;
    private int roundNumber;
    private String panelMemberName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}