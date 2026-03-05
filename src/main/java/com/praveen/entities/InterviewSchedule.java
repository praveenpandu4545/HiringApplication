package com.praveen.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "interview_schedules",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"panel_member_id", "start_time"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore   // Prevent infinite recursion
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "panel_member_id", nullable = false)
    @JsonIgnore
    private Employee panelMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_by_hr_id", nullable = false)
    @JsonIgnore
    private Employee scheduledBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_round_status_id", nullable = false)
    private StudentRoundStatus studentRoundStatus;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id", nullable = false)
    private Drive drive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewStatus status;
    
    @Column(columnDefinition = "TEXT")
    private String review;
}