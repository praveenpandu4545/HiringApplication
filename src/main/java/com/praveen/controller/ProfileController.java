package com.praveen.controller;

import com.praveen.dto.ProfileResponse;
import com.praveen.dto.ResetPasswordRequest;
import com.praveen.dto.UpdateForgottenPassword;
import com.praveen.dto.ValidateOTP;
import com.praveen.dto.generateOtpRequest;
import com.praveen.entities.Employee;
import com.praveen.entities.ForgotPasswordOTP;
import com.praveen.entities.Student;
import com.praveen.entities.User;
import com.praveen.repository.EmployeeRepository;
import com.praveen.repository.StudentRepository;
import com.praveen.repository.UserRepository;
import com.praveen.service.ForgotPasswordService;

import java.util.Optional;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ForgotPasswordService FPO;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        String email = authentication.getName();

        // Try student first
        Student student = studentRepository
                .findByEmail(email)
                .orElse(null);

        if (student != null) {
            ProfileResponse response = new ProfileResponse(
                    student.getName(),
                    student.getEmail(),
                    student.getUser().getRole().name(),
                    student.getDepartment(),
                    student.getPhone(),
                    student.getStudentId()
            );
            return ResponseEntity.ok(response);
        }

        // Try employee
        Employee employee = employeeRepository
                .findByUserEmail(email)
                .orElse(null);

        if (employee != null) {
            ProfileResponse response = new ProfileResponse(
                    employee.getName(),
                    employee.getEmail(),
                    employee.getUser().getRole().name(),
                    employee.getDepartment(),
                    employee.getPhone(),
                    null
            );
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body("User not found");
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Old password is incorrect");
        }

        // Check new and confirm match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("New passwords do not match");
        }

        // Encode new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }
    
    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(
            @RequestBody generateOtpRequest request) {

        String response =
                FPO.generateOTP(
                        request.getEmail(),
                        request.getResetting()
                );

        if(response.equals("E-Mail already exists")) {

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate-otp")
    public ResponseEntity<Boolean> validateOtp(@RequestBody ValidateOTP req){	
    	Boolean response = FPO.validateOTP(req.getEmail(), req.getOtp());
    	return ResponseEntity.ok(response);
    }
    
    @PostMapping("/update-forgotten-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdateForgottenPassword request){
    	Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
    	if(optionalUser.isPresent()) {
    		User user = optionalUser.get();
    		user.setPassword(passwordEncoder.encode(request.getPassword()));
    		userRepository.save(user);
    		return ResponseEntity.ok("Password updated succesfully");
    	}
    	else {
    		return ResponseEntity.badRequest().body("User not found");
    	}
    }
    
    
}