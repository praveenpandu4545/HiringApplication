package com.praveen.entities;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private String email;

    @OneToMany(mappedBy = "student", orphanRemoval = true)
    private List<StudentDrive> studentDrives = new ArrayList<>();
}
