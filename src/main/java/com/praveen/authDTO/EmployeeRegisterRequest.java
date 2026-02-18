package com.praveen.authDTO;

import com.praveen.entities.Role;
import lombok.Data;

@Data
public class EmployeeRegisterRequest {

    private String name;
    private String department;
    private String phone;

    private String email;
    private String password;

    // Must be either HR or PANEL
    private Role role;
}
