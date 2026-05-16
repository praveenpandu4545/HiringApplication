package com.praveen.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.praveen.service.EmailService;

@Component
public class EmailUtil {

    @Autowired
    private EmailService emailService;

    public void sendRegistrationSuccess(String to, String name, String password) {

        String subject = "Registration Successful";

        String body = "Hi " + name + ",\n\n"
                + "Your account has been successfully created. You can use the password " + password 
                + " to login into your account. And kindly change ur password after logging in for the first time." + "\n\n"
                + "Regards,\nAutoHire AI";

        emailService.sendSimpleEmail(to, subject, body);
    }

    public void sendManualRegistrationSuccess(String to, String name) {

        String subject = "Registration Successful";

        String body = "Hi " + name + ",\n\n"
                + "Your account has been successfully created." + "\n\\n\""
                + "Regards,\nAutoHire AI";

        emailService.sendSimpleEmail(to, subject, body);
    }
    
    
	public void sendDriveRegistrationSuccess(String to, String name, String driveName) {
		// TODO Auto-generated method stub
		
		String subject = "Drive Registration Update";
		
		String body = "Hi " + name + ",\n\n"
				+ "We would like to inform you that we have registered you for the drive " + driveName +" .\n\n"
				+ "Kindly log into your account for more details" + "\n\n"
                + "Regards,\nAutoHire AI";
		
        emailService.sendSimpleEmail(to, subject, body);

	}
	
	public void generateOTP(String to, String otp) {
		String subject = "AutoHire validation request";
		
		String body = "Hello,\n\n" +
		            "We received a request to authenticate your account .\n\n" +
		            "Your One-Time Password (OTP) is: " + otp + "\n\n" +
		            "This OTP is valid for the next 5 minutes.\n" +
		            "Please do not share this OTP with anyone for security reasons.\n\n" +
		            "Regards,\n" +
		            "Team AutoHire AI";
		emailService.sendSimpleEmail(to, subject, body);
		 
	}
}

