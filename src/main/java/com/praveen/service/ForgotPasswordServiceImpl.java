package com.praveen.service;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.praveen.entities.ForgotPasswordOTP;
import com.praveen.entities.User;
import com.praveen.repository.ForgotPasswordOTPRepository;
import com.praveen.repository.UserRepository;
import com.praveen.util.EmailUtil;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService{
	
	@Autowired
	public ForgotPasswordOTPRepository repo;
	
	@Autowired
	public UserRepository userRepo;
	
	@Autowired
	public EmailUtil emailUtil;

	@Override
	public String generateOTP(String email, String resetting) {
		if(resetting.equals("false")) {
			Optional<User> user = userRepo.findByEmail(email);
			if(user.isPresent()) {
				return "E-Mail already exists" ;	
			}
		}
		String otp = randomOTP();
		Optional<ForgotPasswordOTP> OptionalUser = repo.findByEmail(email);
		if (OptionalUser.isPresent()) {
	        ForgotPasswordOTP forgotPasswordOTP = OptionalUser.get();
	        forgotPasswordOTP.setOtp(otp);
	        repo.save(forgotPasswordOTP);

	    } 
		else{
	        ForgotPasswordOTP forgotPasswordOTP = new ForgotPasswordOTP();
	        forgotPasswordOTP.setEmail(email);
	        forgotPasswordOTP.setOtp(otp);
	        repo.save(forgotPasswordOTP);
	    }
		
		emailUtil.generateOTP(email, otp);
		
	    return "OTP sent to the mail successfully";
	}
	
	
	public static String randomOTP() {
	    String numbers = "0123456789";
	    Random random = new Random();
	    StringBuilder otp = new StringBuilder();
	    for (int i = 0; i < 4; i++) {
	        otp.append(numbers.charAt(random.nextInt(numbers.length())));
	    }
	    return otp.toString();
	}


	@Override
	public Boolean validateOTP(String email, String otp) {
//		System.out.println(email);
//    	System.out.println(otp);
		Optional<ForgotPasswordOTP> OptionalUser = repo.findByEmail(email);
		String storedOtp = OptionalUser.get().getOtp();
//		System.out.println("Stored otp is " + storedOtp);
//		System.out.println("Entered otp is " + otp);
		if(storedOtp.equals(otp)) return true;
		else return false;
	}
}
