package com.praveen.service;

import com.praveen.dto.HrMemberResponse;
import com.praveen.dto.PanelMemberResponse;
import java.util.List;

public interface EmployeeService {

    List<PanelMemberResponse> getPanelMembers();
    List<HrMemberResponse> getHrMembers();

}