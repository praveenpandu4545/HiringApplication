package com.praveen.repository;

import com.praveen.entities.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

    // 🔥 PANEL CONFLICT CHECK
    List<InterviewSchedule>
    findByPanelMemberIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long panelMemberId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    // 🔥 STUDENT CONFLICT CHECK
    List<InterviewSchedule>
    findByStudentIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long studentId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
    
    Optional<InterviewSchedule> findByStudentRoundStatusId(Long roundId);
    
    List<InterviewSchedule> findByScheduledById(Long hrId);

}