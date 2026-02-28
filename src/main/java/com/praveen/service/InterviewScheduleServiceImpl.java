package com.praveen.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.praveen.dto.HRInterviewResponse;
import com.praveen.dto.InterviewConflictResponse;
import com.praveen.entities.Drive;
import com.praveen.entities.Employee;
import com.praveen.entities.InterviewSchedule;
import com.praveen.entities.InterviewStatus;
import com.praveen.entities.Student;
import com.praveen.entities.StudentRoundStatus;
import com.praveen.exceptions.InterviewConflictException;
import com.praveen.repository.EmployeeRepository;
import com.praveen.repository.InterviewScheduleRepository;
import com.praveen.repository.StudentRepository;
import com.praveen.repository.StudentRoundStatusRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewScheduleServiceImpl implements InterviewScheduleService {

    private final InterviewScheduleRepository scheduleRepo;
    private final StudentRepository studentRepo;
    private final EmployeeRepository employeeRepo;
    private final StudentRoundStatusRepository roundRepo;

    @Override
    @Transactional
    public InterviewSchedule scheduleInterview(Long studentId,
                                               Long panelMemberId,
                                               Long roundId,
                                               LocalDateTime startTime,
                                               LocalDateTime endTime) {

        // 1️⃣ Validate time
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // 2️⃣ Fetch round
        StudentRoundStatus round = roundRepo.findById(roundId)
                .orElseThrow(() -> new RuntimeException("Round not found"));
        
        Drive drive = round.getStudentDrive().getDrive();

        // 3️⃣ Validate student belongs to this round
        Student actualStudent = round.getStudentDrive().getStudent();

        if (!actualStudent.getId().equals(studentId)) {
            throw new RuntimeException("Student does not belong to this round");
        }

        // 4️⃣ Fetch panel member
        Employee panelMember = employeeRepo.findById(panelMemberId)
                .orElseThrow(() -> new RuntimeException("Panel member not found"));

        // 5️⃣ Extract HR from JWT
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        Employee hr = employeeRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        // 6️⃣ PANEL CONFLICT CHECK
        List<InterviewSchedule> panelConflicts =
                scheduleRepo.findByPanelMemberIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        panelMemberId,
                        endTime,
                        startTime
                );

        if (!panelConflicts.isEmpty()) {

            List<InterviewConflictResponse.ConflictDetails> details =
                    panelConflicts.stream().map(schedule ->
                            InterviewConflictResponse.ConflictDetails.builder()
                                    .interviewId(schedule.getId())
                                    .studentName(schedule.getStudent().getName())
                                    .existingStartTime(schedule.getStartTime())
                                    .existingEndTime(schedule.getEndTime())
                                    .build()
                    ).toList();

            InterviewConflictResponse response =
                    InterviewConflictResponse.builder()
                            .message("Panel member is not available during requested time")
                            .requestedStartTime(startTime)
                            .requestedEndTime(endTime)
                            .conflicts(details)
                            .build();

            throw new InterviewConflictException(response);
        }

        // 7️⃣ STUDENT CONFLICT CHECK
        List<InterviewSchedule> studentConflicts =
                scheduleRepo.findByStudentIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        studentId,
                        endTime,
                        startTime
                );

        if (!studentConflicts.isEmpty()) {
            throw new RuntimeException("Student already has an interview at this time");
        }

        // 8️⃣ Create InterviewSchedule
        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setStudent(actualStudent);           // Keep both fields synced
        schedule.setStudentRoundStatus(round);
        schedule.setPanelMember(panelMember);
        schedule.setScheduledBy(hr);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setStatus(InterviewStatus.SCHEDULED);
        schedule.setDrive(drive);

        // 9️⃣ Maintain bidirectional relationships
        actualStudent.getInterviews().add(schedule);
        panelMember.getAssignedInterviews().add(schedule);
        hr.getScheduledInterviews().add(schedule);
        drive.getInterviewSchedules().add(schedule);

        return scheduleRepo.save(schedule);
    }
    
    @Override
    public List<HRInterviewResponse> getInterviewsScheduledByHR() {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Employee hr = employeeRepo.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("HR not found"));

        List<InterviewSchedule> schedules =
                scheduleRepo.findByScheduledById(hr.getId());

        return schedules.stream().map(schedule -> {

            Drive drive = schedule.getDrive();
            Student student = schedule.getStudent();
            Employee panel = schedule.getPanelMember();
            StudentRoundStatus round = schedule.getStudentRoundStatus();

            return HRInterviewResponse.builder()
                    .collegeName(drive.getCollegeName()) // adjust if college entity exists
                    .driveName(drive.getDriveName())
                    .studentName(student.getName())
                    .studentEmail(student.getEmail())
                    .roundNumber(round.getRoundNumber())
                    .panelMemberName(panel.getName())
                    .startTime(schedule.getStartTime())
                    .endTime(schedule.getEndTime())
                    .build();

        }).toList();
    }
}