package com.praveen.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class RoundResponse {
	private Long id;
    private int roundNumber;
    private String roundName;
}
