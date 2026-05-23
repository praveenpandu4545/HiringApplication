package com.praveen.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "college")
public class College {
	
	public College(String collegeName) {
		this.collegeName = collegeName;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false)
	private String collegeName;
	
	@OneToMany(mappedBy = "college", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CollegeDrive> collegeDrives = new ArrayList<>();
	
    @Column(nullable = false)
    private boolean deleted = false;
}
