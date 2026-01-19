package com.praveen.dto;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class StudentResponse {
    private String studentId;
    private String name;
    private String department;
    private String phone;
    private List<StudentRoundStatusResponse> statuses;
}

