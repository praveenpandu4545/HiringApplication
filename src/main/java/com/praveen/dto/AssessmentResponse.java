package com.praveen.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResponse {

    private Long id;
    private String title;
    private String description;

    private String driveName;
    private String collegeName;

    private String roundName;
    private Integer roundNumber;

    private int duration;
    private double totalMarks;

    private LocalDateTime startTime;
}