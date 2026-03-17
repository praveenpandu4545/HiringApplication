package com.praveen.service;

import com.praveen.dto.HrMemberResponse;
import com.praveen.dto.PanelMemberResponse;
import com.praveen.entities.Role;
import com.praveen.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepo;

    @Override
    public List<PanelMemberResponse> getPanelMembers() {

        return employeeRepo.findByUserRole(Role.PANEL)
                .stream()
                .map(emp -> PanelMemberResponse.builder()
                        .id(emp.getId())
                        .name(emp.getName())
                        .department(emp.getDepartment())
                        .build())
                .toList();
    }

	@Override
	public List<HrMemberResponse> getHrMembers() {
		return employeeRepo.findByUserRole(Role.HR)
                .stream()
                .map(emp -> HrMemberResponse.builder()
                        .id(emp.getId())
                        .name(emp.getName())
                        .department(emp.getDepartment())
                        .email(emp.getEmail())
                        .build())
                .toList();
	}
}