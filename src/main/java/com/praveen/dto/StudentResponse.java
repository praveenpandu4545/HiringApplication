package com.praveen.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StudentResponse {
	private Long id;
    private String studentId;
    private String name;
    private String department;
    private String phone;
    private String email;
}

