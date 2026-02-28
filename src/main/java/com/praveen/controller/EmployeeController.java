package com.praveen.controller;

import com.praveen.dto.PanelMemberResponse;
import com.praveen.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/springApi/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/panel-members")
    public List<PanelMemberResponse> getPanelMembers() {
        return employeeService.getPanelMembers();
    }
}