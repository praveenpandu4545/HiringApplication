package com.praveen.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(
    name = "student_assessments",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"assessment_id", "student_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    @JsonIgnore
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    // ===== PERFORMANCE =====

    private double score;
    private int correctAnswers;
    private int wrongAnswers;
    private int attempted;
    
 // ===== MARKING =====

    private double marksForCorrectAnswer;
    private Double negativeMarks;
    private double totalMarks;
    private double passingMarks;

    // ===== TIMING =====

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    private Date submissionTime;

    // ===== STATUS =====

    private String status; // IN_PROGRESS, COMPLETED, ABSENT

    // ===== ANSWERS =====

    @OneToMany(mappedBy = "studentAssessment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> answers;
    
    @OneToOne
    @JoinColumn(name = "student_round_status_id")
    private StudentRoundStatus studentRoundStatus;
}