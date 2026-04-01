package com.praveen.service;

import com.praveen.dto.AssessmentRequest;
import com.praveen.dto.AssessmentResponse;
import com.praveen.dto.AssessmentSubmissionRequest;
import com.praveen.dto.AssessmentSubmissionResponse;
import com.praveen.dto.StudentResultResponse;
import com.praveen.entities.Assessment;
import com.praveen.entities.AssessmentQuestion;
import com.praveen.entities.Drive;
import com.praveen.entities.Question;
import com.praveen.entities.Round;
import com.praveen.entities.Student;
import com.praveen.entities.StudentAssessment;
import com.praveen.entities.StudentDrive;
import com.praveen.entities.StudentResult;
import com.praveen.repository.AssessmentRepository;
import com.praveen.repository.DriveRepository;
import com.praveen.repository.QuestionRepository;
import com.praveen.repository.RoundRepository;
import com.praveen.repository.StudentAssessmentRepository;
import com.praveen.repository.StudentRepository;
import com.praveen.repository.StudentResultRepository;
import com.praveen.service.AssessmentService;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final DriveRepository driveRepository;
    private final RoundRepository roundRepository;
    private final StudentAssessmentRepository studentAssessmentRepository;
    private final QuestionRepository questionRepository;
    private final StudentResultRepository studentResultRepository;
    private final StudentRepository studentRepository;

    public AssessmentServiceImpl(AssessmentRepository assessmentRepository,
                                 DriveRepository driveRepository,
                                 RoundRepository roundRepository,
                                 StudentAssessmentRepository studentAssessmentRepository,
                                 QuestionRepository questionRepository,
                                 StudentResultRepository studentResultRepository,
                                 StudentRepository studentRepository) {
        this.assessmentRepository = assessmentRepository;
        this.driveRepository = driveRepository;
        this.roundRepository = roundRepository;
        this.studentAssessmentRepository = studentAssessmentRepository;
        this.questionRepository = questionRepository;
        this.studentResultRepository = studentResultRepository;
        this.studentRepository = studentRepository;
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
                .isActive(true)
                .build();
        assessment = assessmentRepository.save(assessment);
        
        List<Question> questions = questionRepository.findByDomainIn(request.getSelectedDomains());

		     // 🔥 REMOVE DUPLICATES USING MAP
		     Map<Long, Question> uniqueQuestionsMap = new LinkedHashMap<>();
		
		     for (Question q : questions) {
		         uniqueQuestionsMap.put(q.getId(), q); // duplicates overwritten
		     }
		
		     // Convert back to list
		     List<Question> uniqueQuestions = new ArrayList<>(uniqueQuestionsMap.values());
		
		     // 🔥 Shuffle after removing duplicates
		     Collections.shuffle(uniqueQuestions);
		
		     // Limit questions
		     int maxQuestions = request.getTotalQuestions();
		
		     List<Question> selectedQuestions = uniqueQuestions.stream()
		             .limit(maxQuestions)
		             .toList();
        
        List<AssessmentQuestion> assessmentQuestions = new ArrayList<>();
        for (Question question : selectedQuestions) {
            AssessmentQuestion aq = AssessmentQuestion.builder()
                    .assessment(assessment)
                    .question(question)
                    .mandatory(true) 
                    .build();

            assessmentQuestions.add(aq);
        }
        assessment.setAssessmentQuestions(assessmentQuestions);
        assessment = assessmentRepository.save(assessment);
        
        List<StudentDrive> studentDrives = drive.getStudentDrives();
        for(StudentDrive sd : studentDrives) {
        	Student student = sd.getStudent();
        	StudentAssessment studentAssessment = new StudentAssessment();
        	studentAssessment.setAssessment(assessment);
        	studentAssessment.setStudent(student);
        	studentAssessment.setStatus("ABSENT");
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
    
    @Override
    @Transactional
    public AssessmentSubmissionResponse submitAssessment(
            AssessmentSubmissionRequest request,
            String email) {

        // 1. Fetch student
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // 2. Fetch assessment
        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        // 3. Fetch StudentAssessment (🔥 IMPORTANT)
        StudentAssessment studentAssessment = studentAssessmentRepository
                .findByStudentAndAssessment(student, assessment)
                .orElseThrow(() -> new RuntimeException("StudentAssessment not found"));

        // 4. Validations
        if (!assessment.isActive()) {
            throw new IllegalArgumentException("Assessment not active");
        }

        if (LocalDateTime.now().isAfter(assessment.getEndTime())) {
            throw new IllegalArgumentException("Assessment time is over");
        }

        // 🔥 Prevent re-attempt
        if ("COMPLETED".equals(studentAssessment.getStatus())) {
            throw new IllegalArgumentException("You already attempted this test");
        }

        // 5. Prepare correct answers
        Map<Long, String> correctAnswersMap = assessment.getAssessmentQuestions()
                .stream()
                .collect(Collectors.toMap(
                        aq -> aq.getQuestion().getId(),
                        aq -> aq.getQuestion().getCorrectAnswer()
                ));

        double score = 0;
        int correct = 0;
        int wrong = 0;

        // 6. Evaluate answers
        for (AssessmentSubmissionRequest.AnswerRequest ans : request.getAnswers()) {

            String correctAnswer = correctAnswersMap.get(ans.getQuestionId());

            if (correctAnswer != null) {

                if (correctAnswer.equals(ans.getSelectedOption())) {
                    score += assessment.getMarksForCorrectAnswer();
                    correct++;
                } else {
                    if (assessment.getNegativeMarks() != null) {
                        score -= assessment.getNegativeMarks();
                    }
                    wrong++;
                }
            }
        }

        // 7. Qualification
        boolean qualified = score >= assessment.getPassingMarks();

        // 8. Save StudentResult
        StudentResult result = StudentResult.builder()
                .student(student)
                .assessment(assessment)
                .obtainedMarks(score)
                .qualified(qualified)
                .attemptNumber(1) // only one attempt allowed
                .submittedAt(LocalDateTime.now())
                .build();

        studentResultRepository.save(result);

        // 9. 🔥 UPDATE STUDENT ASSESSMENT STATUS
        studentAssessment.setStatus("COMPLETED");
        studentAssessment.setSubmissionTime(new Date());
        studentAssessment.setScore(score);
        studentAssessment.setCorrectAnswers(correct);
        studentAssessment.setWrongAnswers(wrong);

        studentAssessmentRepository.save(studentAssessment);

        // 10. Response (NO MARKS IF YOU DON’T WANT)
        return AssessmentSubmissionResponse.builder()
                .score(score)
                .qualified(qualified)
                .attemptNumber(1)
                .message("Assessment submitted successfully")
                .build();
    }
    
    @Override
    public List<StudentResultResponse> getResultsByAssessment(Long assessmentId) {

        List<StudentResult> results =
                studentResultRepository.findByAssessment_Id(assessmentId);

        return results.stream()
                .map(r -> StudentResultResponse.builder()
                        .studentName(r.getStudent().getName())
                        .marks(r.getObtainedMarks())
                        .qualified(r.isQualified())
                        .attemptNumber(r.getAttemptNumber())
                        .build())
                .toList();
    }
}