package com.praveen.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.praveen.dto.HRInterviewResponse;
import com.praveen.dto.InterviewConflictResponse;
import com.praveen.dto.PanelInterviewResponseDTO;
import com.praveen.entities.Drive;
import com.praveen.entities.Employee;
import com.praveen.entities.InterviewSchedule;
import com.praveen.entities.InterviewStatus;
import com.praveen.entities.Student;
import com.praveen.entities.StudentRoundStatus;
import com.praveen.exceptions.InterviewConflictException;
import com.praveen.repository.DriveRepository;
import com.praveen.repository.EmployeeRepository;
import com.praveen.repository.InterviewScheduleRepository;
import com.praveen.repository.StudentRepository;
import com.praveen.repository.StudentRoundStatusRepository;
import com.praveen.security.JwtUtil;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.praveen.dto.AutoScheduleRequest;
import com.praveen.dto.BreakSlot;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class InterviewScheduleServiceImpl implements InterviewScheduleService {

    private final InterviewScheduleRepository scheduleRepo;
    private final StudentRepository studentRepo;
    private final EmployeeRepository employeeRepo;
    private final StudentRoundStatusRepository roundRepo;
    private final DriveRepository driveRepo;
    private final JwtUtil jwtUtil;
    
    private static final Logger log =
            LoggerFactory.getLogger(InterviewScheduleServiceImpl.class);

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
            		.interviewId(schedule.getId())
                    .collegeName(drive.getCollegeName()) 
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
    
    @Override
    @Transactional
    public InterviewSchedule rescheduleInterview(
            Long interviewId,
            Long panelMemberId,      // NEW
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        // 1️⃣ Validate time
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // 2️⃣ Fetch existing interview
        InterviewSchedule existing = scheduleRepo.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        Long studentId = existing.getStudent().getId();

        // 3️⃣ Determine which panel to check
        Long finalPanelId;

        if (panelMemberId != null) {
            finalPanelId = panelMemberId;
        } else {
            finalPanelId = existing.getPanelMember().getId();
        }

        // 4️⃣ PANEL CONFLICT CHECK (Exclude current interview)
        List<InterviewSchedule> panelConflicts =
                scheduleRepo.findByPanelMemberIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                        finalPanelId,
                        interviewId,
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

        // 5️⃣ STUDENT CONFLICT CHECK (Exclude current interview)
        List<InterviewSchedule> studentConflicts =
                scheduleRepo.findByStudentIdAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                        studentId,
                        interviewId,
                        endTime,
                        startTime
                );

        if (!studentConflicts.isEmpty()) {
            throw new RuntimeException("Student already has an interview at this time");
        }

        // 6️⃣ Update panel member if changed
        if (panelMemberId != null &&
            !panelMemberId.equals(existing.getPanelMember().getId())) {

            Employee newPanel = employeeRepo.findById(panelMemberId)
                    .orElseThrow(() -> new RuntimeException("Panel member not found"));

            existing.setPanelMember(newPanel);
        }

        // 7️⃣ Update time
        existing.setStartTime(startTime);
        existing.setEndTime(endTime);

        return scheduleRepo.save(existing);
    }
    
    @Override
    @Transactional(noRollbackFor = InterviewConflictException.class)
    public void autoSchedule(AutoScheduleRequest request) {

        log.info("========== AUTO SCHEDULING STARTED ==========");

        // 1️⃣ Fetch Drive
        Drive drive = driveRepo.findById(request.getDriveId())
                .orElseThrow(() -> new RuntimeException("Drive not found"));

//        log.info("Drive ID: {}", request.getDriveId());
//        log.info("Round Number: {}", request.getRoundNumber());
//        log.info("Start Date: {}", request.getStartDate());
//        log.info("Students Count: {}", request.getStudentIds().size());
//        log.info("Panels Count: {}", request.getPanelMemberIds().size());

        if (request.getStartDate() == null || request.getStartDate().isEmpty()) {
            throw new RuntimeException("Start date is required");
        }

        LocalDate currentDate = LocalDate.parse(request.getStartDate());

        // 2️⃣ Fetch Round Statuses
        List<StudentRoundStatus> roundStatuses =
                roundRepo.findByStudentDrive_Drive_IdAndRoundNumber(
                        request.getDriveId(),
                        request.getRoundNumber()
                );

        Map<Long, StudentRoundStatus> roundMap =
                roundStatuses.stream()
                        .collect(Collectors.toMap(
                                rs -> rs.getStudentDrive()
                                        .getStudent()
                                        .getId(),
                                rs -> rs
                        ));

        List<Long> students = request.getStudentIds();
        List<Long> panels = request.getPanelMemberIds();

        if (students.isEmpty())
            throw new RuntimeException("No students selected");

        if (panels.isEmpty())
            throw new RuntimeException("No panel members selected");

        LocalTime workStart = LocalTime.parse(request.getStartTime());
        LocalTime workEnd = LocalTime.parse(request.getEndTime());
        int duration = request.getInterviewDuration();

        List<BreakSlot> breaks = request.getBreaks() != null
                ? request.getBreaks()
                : new ArrayList<>();

        LocalTime currentTime = workStart;
        int studentIndex = 0;

        // 🔥 Safety protection against infinite loop
        int safetyCounter = 0;
        int maxIterations = 100000;

        while (studentIndex < students.size()) {

            safetyCounter++;
            if (safetyCounter > maxIterations) {
//                log.error("Auto scheduling stopped due to excessive iterations!");
                break;
            }

//            log.info("Current Date: {} | Current Time: {} | Student Index: {}",
//                    currentDate, currentTime, studentIndex);

            // 🔁 Move to next day if work hours exceeded
            if (currentTime.plusMinutes(duration).isAfter(workEnd)) {
//                log.info("Work hours ended. Moving to next day.");
                currentDate = currentDate.plusDays(1);
                currentTime = workStart;
                continue;
            }

         // 🛑 Skip break slots (SAFE VERSION)
            boolean inBreak = false;

            for (BreakSlot brk : breaks) {

                LocalTime breakStart = LocalTime.parse(brk.getStart());
                LocalTime breakEnd = LocalTime.parse(brk.getEnd());

                // Only treat time strictly inside break
                if (!currentTime.isBefore(breakStart) && currentTime.isBefore(breakEnd)) {

//                    log.info("Break time detected. Moving from {} to {}",
//                            currentTime, breakEnd);

                    currentTime = breakEnd;
                    inBreak = true;
                    break;
                }
            }

            if (inBreak) {
                continue;
            }	

            // 🧠 Try scheduling in this time slot
            for (Long panelId : panels) {

                if (studentIndex >= students.size())
                    break;

                Long studentId = students.get(studentIndex);
                StudentRoundStatus round = roundMap.get(studentId);

                if (round == null)
                    throw new RuntimeException(
                            "Round status not found for student: " + studentId
                    );

                LocalDateTime slotStart =
                        LocalDateTime.of(currentDate, currentTime);

                LocalDateTime slotEnd =
                        slotStart.plusMinutes(duration);

//                log.info("Attempting to schedule Student {} with Panel {} at {}",
//                        studentId, panelId, slotStart);

                // 🔍 Check if already scheduled
                Optional<InterviewSchedule> alreadyScheduled =
                        scheduleRepo.findByStudentRoundStatusId(round.getId());

                if (alreadyScheduled.isPresent()) {
//                    log.warn("Student {} already scheduled. Skipping.", studentId);
                    studentIndex++;
                    continue;
                }

                try {
                    scheduleInterview(
                            studentId,
                            panelId,
                            round.getId(),
                            slotStart,
                            slotEnd
                    );

//                    log.info("SUCCESS: Student {} scheduled with Panel {} at {}",
//                            studentId, panelId, slotStart);

                    studentIndex++;

                } catch (InterviewConflictException ex) {
//                    log.warn("Conflict detected for Student {} with Panel {} at {}",
//                            studentId, panelId, slotStart);
                }
            }

            // 🔄 Move to next time slot
            currentTime = currentTime.plusMinutes(duration);
        }

        log.info("========== AUTO SCHEDULING COMPLETED ==========");
    }
    
    @Override
    public List<PanelInterviewResponseDTO> 
        getPanelInterviews(String token) {

        String jwt = token.substring(7); // Remove "Bearer "
        String email = jwtUtil.extractEmail(jwt);

        Employee panel = employeeRepo.findByUserEmail(email)
                .orElseThrow(() -> 
                    new RuntimeException("Panel not found"));

        List<InterviewSchedule> schedules =
        		scheduleRepo.findByPanelMember_IdOrderByStartTimeAsc(
                                panel.getId());

        return schedules.stream()
                .map((InterviewSchedule schedule) -> 
                        PanelInterviewResponseDTO.builder()
                                .driveName(schedule.getDrive().getDriveName())
                                .studentName(schedule.getStudent().getName())
                                .studentEmail(schedule.getStudent().getEmail())
                                .roundNumber(schedule.getStudentRoundStatus().getRoundNumber())
                                .startTime(schedule.getStartTime())
                                .endTime(schedule.getEndTime())
                                .build()
                )
                .collect(Collectors.toList());
    }
}