package com.praveen.authDTO;
import lombok.Data;

@Data
public class StudentRegisterRequest {

    private String studentId;
    private String name;
    private String department;
    private String phone;
    private String email;
    private String password;
    private String collegeName;
}

