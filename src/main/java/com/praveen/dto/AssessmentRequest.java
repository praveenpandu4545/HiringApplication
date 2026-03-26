package com.praveen.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequest {

    private Long driveId;
    private Long roundId;

    private String title;
    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;

    private Integer totalQuestions;

    private Double marksForCorrectAnswer;
    private Double negativeMarks;
    private Double totalMarks;
    private Double passingMarks;

    private boolean shuffleQuestions;
    private boolean shuffleOptions;
    private boolean allowBackNavigation;
    private boolean autoSubmitOnTimeUp;

    private Integer maxAttempts;

    private List<String> selectedDomains;
}