package com.praveen.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllStudentResponse {
	private Long id;
	private String email;
	private String name;
}
