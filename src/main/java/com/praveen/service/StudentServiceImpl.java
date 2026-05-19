package com.praveen.service;

import com.praveen.dto.AllStudentResponse;
import com.praveen.dto.ChangePasswordRequest;
import com.praveen.dto.DriveResponse;
import com.praveen.dto.RoundResponse;
import com.praveen.dto.StudentResponse;
import com.praveen.dto.StudentRoundStatusResponse;
import com.praveen.entities.*;
import com.praveen.repository.*;
import com.praveen.service.StudentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import java.util.*;
import com.praveen.util.*;

@Service
public class StudentServiceImpl implements StudentService {
	
	@Autowired
    private DriveRepository driveRepository;
	
	@Autowired
	private StudentRoundStatusRepository studentRoundStatusRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EmailUtil emailUtil;
	
	@Autowired
	private InterviewScheduleRepository interviewScheduleRepository;

	@Autowired
	private EmployeeRepository employeeRepository;

	@Transactional
	@Override
	public void uploadStudentsForDrive(Long driveId, MultipartFile file) {

	    Drive drive = driveRepository.findById(driveId)
	            .orElseThrow(() -> new RuntimeException("Drive not found"));
	    String collegeName = drive.getCollegeName();  
	    
	    List<Student> parsedStudents = parseExcel(file);

	    for (Student parsedStudent : parsedStudents) {
	    	parsedStudent.setCollegeName(collegeName);

	        String regNo = parsedStudent.getStudentId();

	        Student student = studentRepository
	                .findByStudentId(regNo)
	                .orElse(null);

	        if (student == null) {
	            student = studentRepository.save(parsedStudent);
	        }
	        else {
	        	if(!student.getEmail().equals(parsedStudent.getEmail()) || !student.getStudentId().equals(parsedStudent.getStudentId())) {
	        		System.out.println("Email mismatch for student ID: " + parsedStudent.getStudentId());
	                continue;
	        	}
	        }
	        
	        User user = userRepository.findByEmail(parsedStudent.getEmail()).orElse(null);
	        if(user == null) {
	        	user = new User();
	        	user.setAccountStatus(AccountStatus.ACTIVE);
	        	user.setEmail(parsedStudent.getEmail());
	        	user.setRole(Role.STUDENT);
	        	String rawPassword = PasswordGenerator.generatePassword(10);
	        	System.out.println("password for email : " + parsedStudent.getEmail() + "is : " + rawPassword);
	        	user.setPassword(passwordEncoder.encode(rawPassword));
	        	user = userRepository.save(user);
	        	
	        	emailUtil.sendRegistrationSuccess(parsedStudent.getEmail(), parsedStudent.getName(), rawPassword);
	        }
	        
	        student.setUser(user);
	        studentRepository.save(student);

	        boolean alreadyRegistered = student.getStudentDrives()
	                .stream()
	                .anyMatch(sd -> sd.getDrive().getId().equals(driveId));

	        if (alreadyRegistered) {
	            continue;
	        }

	        StudentDrive sd = new StudentDrive();
	        sd.setDrive(drive);
	        sd.setStudent(student);
	        sd.setFinalStatus("IN PROGRESS");

	        for (Round r : drive.getRounds()) {

	            StudentRoundStatus srs = new StudentRoundStatus();
	            srs.setRoundName(r.getRoundName());
	            srs.setRoundNumber(r.getRoundNumber());
	            srs.setStatus("PENDING");
	            srs.setStudentDrive(sd);
	            srs.setCanSchedule(r.getCanSchedule());

	            sd.getStudentRoundStatuses().add(srs);
	        }

	        drive.getStudentDrives().add(sd);
	        student.getStudentDrives().add(sd);
	        
	        emailUtil.sendDriveRegistrationSuccess(parsedStudent.getEmail(), parsedStudent.getName(), drive.getDriveName());
	    }

	    driveRepository.save(drive);
	}

	
    private List<Student> parseExcel(MultipartFile file) {
        List<Student> students = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                Student student = new Student();
                student.setStudentId(getString(row.getCell(0)).toUpperCase());
                student.setName(getString(row.getCell(1)));
                student.setDepartment(getString(row.getCell(2)));
                student.setPhone(getString(row.getCell(3)));
                student.setEmail(getString(row.getCell(4)));
                
//                String rawPassword = PasswordGenerator.generatePassword(10);
//                student.setPassword(rawPassword);

                students.add(student);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel: " + e.getMessage());
        }

        return students;
    }

    private String getString(Cell cell) {

        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {

            case STRING:
                return cell.getStringCellValue().trim();

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // Convert numeric to long to remove scientific notation
                return String.valueOf((long) cell.getNumericCellValue());

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            default:
                return cell.toString().trim();
        }
    }


	@Override
	public List<StudentResponse> getAllStudentsByDriveId(Long driveId) {
		Drive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new RuntimeException("Drive not found"));
		List<StudentResponse> studentResponses = new ArrayList<>();
		for(StudentDrive studentDrive : drive.getStudentDrives()) {
			StudentResponse sr = new StudentResponse();
			sr.setDepartment(studentDrive.getStudent().getDepartment());
			sr.setName(studentDrive.getStudent().getName());
			sr.setPhone(studentDrive.getStudent().getPhone());
			sr.setStudentId(studentDrive.getStudent().getStudentId());
			sr.setId(studentDrive.getStudent().getId());
			sr.setEmail(studentDrive.getStudent().getEmail());
			studentResponses.add(sr);
		}
		
		return studentResponses;
	}

	@Override
	public List<StudentRoundStatusResponse>
	getAllRoundsByStudentIdAndDriveId(Long studentId, Long driveId) {

	    Student student = studentRepository.findById(studentId)
	            .orElseThrow(() -> new RuntimeException("Student not found"));

	    List<StudentRoundStatusResponse> response = new ArrayList<>();

	    for (StudentDrive studentDrive : student.getStudentDrives()) {

	        if (!studentDrive.getDrive().getId().equals(driveId)) {
	            continue;
	        }

	        for (StudentRoundStatus srs : studentDrive.getStudentRoundStatuses()) {

	            StudentRoundStatusResponse srsr = new StudentRoundStatusResponse();
	            srsr.setId(srs.getId());
	            srsr.setRoundName(srs.getRoundName());
	            srsr.setRoundNumber(srs.getRoundNumber());
	            srsr.setStatus(srs.getStatus());
	            srsr.setCanSchedule(srs.getCanSchedule());

	            // 🔥 CHECK INTERVIEW
	            Optional<InterviewSchedule> interviewOpt =
	                    interviewScheduleRepository
	                            .findByStudentRoundStatusId(srs.getId());

	            if (interviewOpt.isPresent()) {
	                InterviewSchedule interview = interviewOpt.get();

	                srsr.setInterviewScheduled(true);
	                srsr.setInterviewStartTime(interview.getStartTime());
	                srsr.setInterviewEndTime(interview.getEndTime());
	                srsr.setPanelName(interview.getPanelMember().getName());
	                srsr.setPanelReview(interview.getReview());
	            } else {
	                srsr.setInterviewScheduled(false);
	            }

	            response.add(srsr);
	        }
	    }

	    return response;
	}


	@Override
	@Transactional
	public List<DriveResponse> getDrivesByStudentId(Long studentId) {

	    Student student = studentRepository.findById(studentId)
	            .orElseThrow(() -> new RuntimeException("Student not found"));

	    List<DriveResponse> responseList = new ArrayList<>();

	    for (StudentDrive sd : student.getStudentDrives()) {

	        Drive drive = sd.getDrive();

	        DriveResponse driveResponse = new DriveResponse();
	        driveResponse.setId(drive.getId());
	        driveResponse.setCollegeName(drive.getCollegeName());
	        driveResponse.setDriveName(drive.getDriveName());
	        driveResponse.setNoOfRounds(drive.getNoOfRounds());

	        // 🔹 Map rounds
	        List<RoundResponse> roundResponses = new ArrayList<>();

	        for (Round round : drive.getRounds()) {
	            RoundResponse rr = new RoundResponse();
	            rr.setId(round.getId());
	            rr.setRoundNumber(round.getRoundNumber());
	            rr.setRoundName(round.getRoundName());
	            roundResponses.add(rr);
	        }

	        driveResponse.setRounds(roundResponses);

	        responseList.add(driveResponse);
	    }

	    return responseList;
	}


	@Override
	public StudentResponse getStudent(String studentEmail) {
		Student student = studentRepository.findByEmail(studentEmail)
				.orElseThrow(() -> new RuntimeException("Student not found"));
		StudentResponse sr = new StudentResponse();
		sr.setDepartment(student.getDepartment());
		sr.setEmail(student.getEmail());
		sr.setName(student.getName());
		sr.setPhone(student.getPhone());
		sr.setStudentId(student.getStudentId());
		sr.setCollegeName(student.getCollegeName());
		sr.setId(student.getId());
		return sr;
	}


	@Override
	public List<AllStudentResponse> getAllStudents() {
		List<Student> students = studentRepository.findAll();
		List<AllStudentResponse> response = new ArrayList<>();
		for(Student s : students) {
			AllStudentResponse asr = new AllStudentResponse();
			asr.setEmail(s.getEmail());
			asr.setId(s.getId());
			asr.setName(s.getName());
			response.add(asr);
		}
		return response;
	}


	@Override
	public List<AllStudentResponse> getAllPanels() {
		List<Employee> panels = employeeRepository.findAll();
		List<AllStudentResponse> response = new ArrayList<>();
		for(Employee s : panels) {
			Role role = s.getUser().getRole();
			if(role == Role.HR) continue; 
			AllStudentResponse asr = new AllStudentResponse();
			asr.setEmail(s.getEmail());
			asr.setId(s.getId());
			asr.setName(s.getName());
			response.add(asr);
		}
		return response;
	}

//	@Override
//	public String updatePassword(ChangePasswordRequest request) {
//		try {
//			String newPassword = request.getPassword();
//			if(newPassword.length() < 10) {
//				throw new IllegalArgumentException("Password must be at least 10 characters long");
//			}
//			String studentId = request.getStudentId();
//			Student student = studentRepository.findByStudentId(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
//			student.setPassword(newPassword);
//			studentRepository.save(student);
//			return "Password got updated successfully";
//		}
//		catch(Exception e) {
//			return "Password update got failed due to " + e.getMessage();
//		}
//	}

}
