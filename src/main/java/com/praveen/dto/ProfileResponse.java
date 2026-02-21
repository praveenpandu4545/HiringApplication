package com.praveen.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    private String name;
    private String email;
    private String role;
    private String department;
    private String phone;
    private String studentId;   // only for students
}