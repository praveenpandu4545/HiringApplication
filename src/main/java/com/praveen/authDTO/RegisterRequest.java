
package com.praveen.authDTO;

import com.praveen.entities.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String email;
    private String password;
    private Role role;
}
