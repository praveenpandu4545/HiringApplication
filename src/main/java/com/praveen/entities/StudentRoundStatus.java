package com.praveen.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(
	    name = "student_round_status",
	    uniqueConstraints = {
	        @UniqueConstraint(columnNames = {"student_drive_id", "round_number"})
	    }
	)	
public class StudentRoundStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int roundNumber;
    private String roundName;
    private String status; 
    private Boolean canSchedule;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_drive_id")
    private StudentDrive studentDrive;

    @OneToOne(mappedBy = "studentRoundStatus", cascade = CascadeType.ALL)
    private InterviewSchedule interviewSchedule;
    
    @OneToOne(mappedBy = "studentRoundStatus", cascade = CascadeType.ALL)
    private StudentAssessment studentAssessment;
    
}

