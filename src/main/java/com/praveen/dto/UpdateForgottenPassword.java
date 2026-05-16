package com.praveen.dto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateForgottenPassword {
	public String email;
	public String password;
}
