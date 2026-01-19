package com.praveen.dto;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DriveResponse {
    private Long id;
    private String collegeName;
    private String driveName;
    private int noOfRounds;
    private List<RoundResponse> rounds;
}

