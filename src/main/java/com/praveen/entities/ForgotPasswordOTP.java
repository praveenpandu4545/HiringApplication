package com.praveen.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "forgotPasswordOtp")
public class ForgotPasswordOTP {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String otp;
}
