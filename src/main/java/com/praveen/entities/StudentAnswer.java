package com.praveen.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_assessment_id", nullable = false)
    @JsonIgnore
    private StudentAssessment studentAssessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // ===== ANSWER DATA =====

    private String selectedAnswer;

    private boolean isCorrect;

    private double marksAwarded;
}