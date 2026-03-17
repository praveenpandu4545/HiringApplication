package com.praveen.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HrMemberResponse {

    private Long id;
    private String name;
    private String department;
    private String email;
}