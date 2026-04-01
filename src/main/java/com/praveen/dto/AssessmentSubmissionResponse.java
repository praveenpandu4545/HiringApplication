package com.praveen.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentSubmissionResponse {

    private double score;
    private boolean qualified;
    private int attemptNumber;
    private String message;
}