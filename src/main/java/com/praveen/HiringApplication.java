package com.praveen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.praveen.entities.Student;

@SpringBootApplication
public class HiringApplication {

	public static void main(String[] args) {
		SpringApplication.run(HiringApplication.class, args);
		System.out.println("Hi praveen our app is working.....");
		Student s1 = new Student();
		s1.setName("praveen");
		System.out.println(s1.getName());
		System.out.println(s1.getDepartment());
	}

}
