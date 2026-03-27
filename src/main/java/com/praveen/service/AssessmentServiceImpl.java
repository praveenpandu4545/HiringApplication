package com.praveen.service;

import com.praveen.dto.AssessmentRequest;
import com.praveen.dto.AssessmentResponse;
import com.praveen.entities.Assessment;
import com.praveen.entities.Drive;
import com.praveen.entities.Round;
import com.praveen.entities.Student;
import com.praveen.entities.StudentAssessment;
import com.praveen.entities.StudentDrive;
import com.praveen.repository.AssessmentRepository;
import com.praveen.repository.DriveRepository;
import com.praveen.repository.RoundRepository;
import com.praveen.repository.StudentAssessmentRepository;
import com.praveen.service.AssessmentService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final DriveRepository driveRepository;
    private final RoundRepository roundRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;

    public AssessmentServiceImpl(AssessmentRepository assessmentRepository,
                                 DriveRepository driveRepository,
                                 RoundRepository roundRepository,
                                 StudentAssessmentRepository studentAssessmentRepository) {
        this.assessmentRepository = assessmentRepository;
        this.driveRepository = driveRepository;
        this.roundRepository = roundRepository;
        this.studentAssessmentRepository = studentAssessmentRepository;
    }

    @Transactional
    @Override
    public Assessment saveAssessment(AssessmentRequest request) {

        Drive drive = driveRepository.findById(request.getDriveId())
                .orElseThrow(() -> new RuntimeException("Drive not found"));

        Round round = roundRepository.findById(request.getRoundId())
                .orElseThrow(() -> new RuntimeException("Round not found"));

        Assessment assessment = Assessment.builder()
                .drive(drive)
                .round(round)
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .duration(request.getDuration())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
                .marksForCorrectAnswer(request.getMarksForCorrectAnswer())
                .negativeMarks(request.getNegativeMarks())
                .shuffleQuestions(request.isShuffleQuestions())
                .shuffleOptions(request.isShuffleOptions())
                .allowBackNavigation(request.isAllowBackNavigation())
                .autoSubmitOnTimeUp(request.isAutoSubmitOnTimeUp())
                .maxAttempts(request.getMaxAttempts())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        assessment = assessmentRepository.save(assessment);
        
        List<StudentDrive> studentDrives = drive.getStudentDrives();
        for(StudentDrive sd : studentDrives) {
        	Student student = sd.getStudent();
        	StudentAssessment studentAssessment = new StudentAssessment();
        	studentAssessment.setAssessment(assessment);
        	studentAssessment.setMarksForCorrectAnswer(request.getMarksForCorrectAnswer());
        	studentAssessment.setNegativeMarks(request.getNegativeMarks());
        	studentAssessment.setStudent(student);
        	studentAssessmentRepository.save(studentAssessment);
        	student.getStudentAssessments().add(studentAssessment);
        }
        

        return assessment;
    }

    @Override
    public List<AssessmentResponse> getAllAssessments() {

        List<Assessment> list = assessmentRepository.findAll();

        return list.stream().map(a -> AssessmentResponse.builder()
                .id(a.getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .driveName(a.getDrive().getDriveName())
                .collegeName(a.getDrive().getCollegeName())
                .roundName(a.getRound().getRoundName())
                .roundNumber(a.getRound().getRoundNumber())
                .duration(a.getDuration())
                .totalMarks(a.getTotalMarks())
                .startTime(a.getStartTime())
                .build()
        ).toList();
    }
}