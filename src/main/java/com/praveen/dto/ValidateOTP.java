package com.praveen.dto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ValidateOTP {
	private String email;
	private String otp;
}
