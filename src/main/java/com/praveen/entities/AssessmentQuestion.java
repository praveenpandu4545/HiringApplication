package com.praveen.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "assessment_questions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"assessment_id", "question_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===== RELATIONS =====

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    @JsonIgnore
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // ===== CONTROL FIELDS =====

    private int questionOrder;

    private double marks; // optional override

    private boolean mandatory;
}	