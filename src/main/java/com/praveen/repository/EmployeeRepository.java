package com.praveen.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.praveen.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUserEmail(String email);

}
