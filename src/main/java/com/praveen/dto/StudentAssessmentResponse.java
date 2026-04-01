package com.praveen.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentAssessmentResponse {

    private Long id;

    // 🔥 Flattened fields (NO ENTITY)
    private Long assessmentId;
    private String title;
    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;

    private boolean active;

    private String status; // COMPLETED / IN_PROGRESS / ABSENT
}