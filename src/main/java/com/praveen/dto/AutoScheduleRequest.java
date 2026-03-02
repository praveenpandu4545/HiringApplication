package com.praveen.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AutoScheduleRequest {

    private Long driveId;
    private Integer roundNumber;
    private String startDate;
    private List<Long> studentIds;
    private List<Long> panelMemberIds;

    private String startTime;      // "09:00"
    private String endTime;        // "18:00"

    private List<BreakSlot> breaks;

    private Integer interviewDuration; // in minutes
}