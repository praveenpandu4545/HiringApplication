package com.praveen.entities;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    
    private String studentId;
    private String name;
    private String department;
    private String phone;
    private String collegeName;
    
    @Column(unique = true, nullable = false)
    private String email;
//    private String password;

    @OneToMany(mappedBy = "student", orphanRemoval = true)
    private List<StudentDrive> studentDrives = new ArrayList<>();
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
	 // ===== Resume Fields =====
	
	    @Lob
	    @Column(name = "resume", columnDefinition = "LONGBLOB")
	    private byte[] resume;
	    private String resumeName;
	    private String resumeType;
	    
	    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
	    private List<InterviewSchedule> interviews = new ArrayList<>();
}
