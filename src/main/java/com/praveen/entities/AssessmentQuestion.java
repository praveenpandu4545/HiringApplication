package com.praveen.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @ManyToOne
    @JoinColumn(name = "assessment_id")
    @JsonBackReference  // 🔥 ADD THIS
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory;
}