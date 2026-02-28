package com.praveen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.praveen.entities.Employee;
import com.praveen.entities.Role;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUserEmail(String email);
    List<Employee> findByUserRole(Role role);

}
