package com.praveen.repository;

import com.praveen.entities.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

    // 🔥 NORMAL CONFLICT CHECK
    List<InterviewSchedule>
    findByPanelMemberIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long panelMemberId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<InterviewSchedule>
    findByStudentIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long studentId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    // 🔥 RESCHEDULE CONFLICT CHECK (Exclude current)
    List<InterviewSchedule>
    findByPanelMemberIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long panelMemberId,
            Long interviewId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<InterviewSchedule>
    findByStudentIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long studentId,
            Long interviewId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    Optional<InterviewSchedule> findByStudentRoundStatusId(Long roundId);

    List<InterviewSchedule> findByScheduledById(Long hrId);
    
    List<InterviewSchedule> findByPanelMember_IdOrderByStartTimeAsc(Long panelId);
}
