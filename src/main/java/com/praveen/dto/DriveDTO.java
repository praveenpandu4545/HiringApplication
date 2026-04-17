package com.praveen.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriveDTO {
    private Long id;
    private String driveName;
    private String collegeName;
    private int noOfRounds;
    private List<String> requiredSkills = new ArrayList<>();
}