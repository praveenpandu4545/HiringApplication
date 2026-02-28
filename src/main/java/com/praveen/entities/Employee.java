package com.praveen.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String department;
    private String phone;
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
 // Panel interviews
    @OneToMany(mappedBy = "panelMember", cascade = CascadeType.ALL)
    private List<InterviewSchedule> assignedInterviews = new ArrayList<>();

    // HR scheduled interviews
    @OneToMany(mappedBy = "scheduledBy", cascade = CascadeType.ALL)
    private List<InterviewSchedule> scheduledInterviews = new ArrayList<>();
}

