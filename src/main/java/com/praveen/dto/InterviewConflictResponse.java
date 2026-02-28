package com.praveen.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewConflictResponse {

    private String message;
    private LocalDateTime requestedStartTime;
    private LocalDateTime requestedEndTime;

    private List<ConflictDetails> conflicts;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ConflictDetails {
        private Long interviewId;
        private String studentName;
        private LocalDateTime existingStartTime;
        private LocalDateTime existingEndTime;
    }
}