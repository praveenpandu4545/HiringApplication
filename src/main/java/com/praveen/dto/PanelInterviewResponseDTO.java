package com.praveen.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PanelInterviewResponseDTO {

	private Long id;
    private String driveName;
    private String studentName;
    private String studentEmail;
    private Long studentId;
    private Integer roundNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String review;
}