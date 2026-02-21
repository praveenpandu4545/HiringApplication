package com.praveen.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor	
public class StudentRoundStatusResponse {
	private Long id;
	private String roundName;
    private int roundNumber;
    private String status;  // IN PROGRESS / SELECTED / REJETCED
}

