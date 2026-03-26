package com.praveen.service;

import com.praveen.dto.AssessmentRequest;
import com.praveen.dto.AssessmentResponse;
import com.praveen.entities.Assessment;
import com.praveen.entities.Drive;
import com.praveen.entities.Round;
import com.praveen.repository.AssessmentRepository;
import com.praveen.repository.DriveRepository;
import com.praveen.repository.RoundRepository;
import com.praveen.service.AssessmentService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final DriveRepository driveRepository;
    private final RoundRepository roundRepository;

    public AssessmentServiceImpl(AssessmentRepository assessmentRepository,
                                 DriveRepository driveRepository,
                                 RoundRepository roundRepository) {
        this.assessmentRepository = assessmentRepository;
        this.driveRepository = driveRepository;
        this.roundRepository = roundRepository;
    }

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

        return assessmentRepository.save(assessment);
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