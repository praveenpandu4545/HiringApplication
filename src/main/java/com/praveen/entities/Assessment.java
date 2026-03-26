package com.praveen.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "assessments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id", nullable = false)
    @JsonIgnore
    private Drive drive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    @JsonIgnore
    private Round round;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentQuestion> assessmentQuestions = new ArrayList<>();

    // ===== BASIC INFO =====

    private String title;
    private String description;

    // ===== TIMING =====

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int duration; // in minutes

    // ===== MARKING =====

    private double marksForCorrectAnswer;
    private Double negativeMarks;
    private double totalMarks;
    private double passingMarks;

    // ===== RULES =====

    private boolean shuffleQuestions;
    private boolean shuffleOptions;
    private boolean allowBackNavigation;
    private boolean autoSubmitOnTimeUp;

    // ===== ATTEMPT CONTROL =====

    private int maxAttempts;
    private boolean isActive;

    // ===== METADATA =====

    private String createdBy;

    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
}