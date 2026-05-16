package com.praveen.service;

public interface ForgotPasswordService {
	public String generateOTP(String email, String resetting);
	public Boolean validateOTP(String email, String otp);
}
