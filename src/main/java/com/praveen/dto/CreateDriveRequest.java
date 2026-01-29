package com.praveen.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CreateDriveRequest {
    private String collegeName;
    private String driveName;
    private int noOfRounds;
    private List<RoundRequest> rounds;
    private List<String> requiredSkills;
}

