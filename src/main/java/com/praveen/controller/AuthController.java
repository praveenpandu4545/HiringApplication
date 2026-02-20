package com.praveen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.praveen.authDTO.EmployeeRegisterRequest;
import com.praveen.authDTO.LoginRequest;
import com.praveen.authDTO.RegisterRequest;
import com.praveen.authDTO.StudentRegisterRequest;
import com.praveen.entities.*;
import com.praveen.repository.EmployeeRepository;
import com.praveen.repository.StudentRepository;
import com.praveen.repository.UserRepository;
import com.praveen.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;	
    
    @Autowired
    private JwtUtil jwtUtil;


    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setAccountStatus(AccountStatus.ACTIVE);

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

    	 try {
    	        authenticationManager.authenticate(
    	                new UsernamePasswordAuthenticationToken(
    	                        request.getEmail(),
    	                        request.getPassword()
    	                )
    	        );

    	    } catch (Exception e) {
    	        return ResponseEntity
    	                .status(401)
    	                .body("Invalid email or password");
    	    }
    	 
    	 
        // Get user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        // Generate token
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudentManual(
            @RequestBody StudentRegisterRequest request) {

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        // Check if studentId already exists
        if (studentRepository.findByStudentId(request.getStudentId()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Student ID already exists");
        }

        // 1️⃣ Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);

        user = userRepository.save(user);

        // 2️⃣ Create Student
        Student student = new Student();
        student.setStudentId(request.getStudentId());
        student.setName(request.getName());
        student.setDepartment(request.getDepartment());
        student.setPhone(request.getPhone());
        student.setEmail(request.getEmail());
        student.setUser(user);

        studentRepository.save(student);

        return ResponseEntity.ok("Student registered successfully");
    }

    @PostMapping("/register/employee")
    public ResponseEntity<?> registerEmployee(
            @RequestBody EmployeeRegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Email already exists");
        }

        // 1️⃣ Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole()); // HR or PANEL
        user.setAccountStatus(AccountStatus.ACTIVE);

        user = userRepository.save(user);

        // 2️⃣ Create Employee
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setDepartment(request.getDepartment());
        employee.setPhone(request.getPhone());
        employee.setUser(user);
       
		employeeRepository.save(employee);

        return ResponseEntity.ok("Employee registered successfully");
    }

    
}
