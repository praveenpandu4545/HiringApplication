package com.praveen.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentResultResponse {

    private String studentName;
    private double marks;
    private boolean qualified;
    private int attemptNumber;
}