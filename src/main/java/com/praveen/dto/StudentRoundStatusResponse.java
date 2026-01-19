package com.praveen.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor	
public class StudentRoundStatusResponse {
    private int roundNumber;
    private String status;  // PENDING / PASSED / FAILED
}

