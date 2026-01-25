package com.praveen.entities;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "drive")
public class Drive {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String driveName;
	private String collegeName;
	private int noOfRounds;
	
	@OneToMany(mappedBy = "drive", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Round> rounds = new ArrayList<>();
	
	@OneToMany(mappedBy = "drive",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentDrive> studentDrives = new ArrayList<>();
	
//	@OneToMany(mappedBy = "drive", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<Student> students = new ArrayList<>();
//	
//	@OneToMany(mappedBy = "drive", cascade = CascadeType.ALL, orphanRemoval = true)
//	private List<StudentRoundStatus> studentRoundStatuses = new ArrayList<>();
}
