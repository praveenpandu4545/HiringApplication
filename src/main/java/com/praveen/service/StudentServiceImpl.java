package com.praveen.service;

import com.praveen.dto.DriveResponse;
import com.praveen.dto.RoundResponse;
import com.praveen.dto.StudentResponse;
import com.praveen.dto.StudentRoundStatusResponse;
import com.praveen.entities.*;
import com.praveen.repository.*;
import com.praveen.service.StudentService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {
	
	@Autowired
    private DriveRepository driveRepository;
	
	@Autowired
	private StudentRoundStatusRepository studentRoundStatusRepository;
	
	@Autowired
	private StudentRepository studentRepository;

	@Transactional
	@Override
	public void uploadStudentsForDrive(Long driveId, MultipartFile file) {

	    Drive drive = driveRepository.findById(driveId)
	            .orElseThrow(() -> new RuntimeException("Drive not found"));

	    List<Student> parsedStudents = parseExcel(file);

	    for (Student parsedStudent : parsedStudents) {

	        String regNo = parsedStudent.getStudentId();

	        Student student = studentRepository
	                .findByStudentId(regNo)
	                .orElse(null);

	        if (student == null) {
	            student = studentRepository.save(parsedStudent);
	        }

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

	            sd.getStudentRoundStatuses().add(srs);
	        }

	        drive.getStudentDrives().add(sd);
	        student.getStudentDrives().add(sd);
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
                student.setStudentId(getString(row.getCell(0)));
                student.setName(getString(row.getCell(1)));
                student.setDepartment(getString(row.getCell(2)));
                student.setPhone(getString(row.getCell(3)));
                student.setEmail(getString(row.getCell(4)));

                students.add(student);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel: " + e.getMessage());
        }

        return students;
    }

    private String getString(Cell cell) {
        if (cell == null) return null;
        return cell.toString().trim();
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

	        // Filter only for requested drive
	        if (!studentDrive.getDrive().getId().equals(driveId)) {
	            continue;
	        }

	        for (StudentRoundStatus srs : studentDrive.getStudentRoundStatuses()) {

	            StudentRoundStatusResponse srsr = new StudentRoundStatusResponse();
	            srsr.setId(srs.getId());
	            srsr.setRoundName(srs.getRoundName());
	            srsr.setRoundNumber(srs.getRoundNumber());
	            srsr.setStatus(srs.getStatus());

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

}
