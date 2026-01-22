package com.praveen.service;

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

        for (Student s : parsedStudents) {
            s.setDrive(drive);                 // owning side
            drive.getStudents().add(s);        // inverse side
            
            List<Round> rounds = drive.getRounds();
            for(Round r : rounds) {
            	StudentRoundStatus srs = new StudentRoundStatus();
            	srs.setRoundNumber(r.getRoundNumber());
            	srs.setRoundName(r.getRoundName());
            	srs.setStatus("PENDING");
            	srs.setStudent(s);
            	srs.setDrive(drive);
//            	studentRoundStatusRepository.save(srs); // automatically gets saved when drive is getting saved below.
            	s.getRoundStatuses().add(srs);
            	drive.getStudentRoundStatuses().add(srs);
            }
        }

        driveRepository.save(drive);           // cascade inserts students
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
		for(Student student : drive.getStudents()) {
			StudentResponse sr = new StudentResponse();
			sr.setDepartment(student.getDepartment());
			sr.setName(student.getName());
			sr.setPhone(student.getPhone());
			sr.setStudentId(student.getStudentId());
			sr.setId(student.getId());
			sr.setEmail(student.getEmail());
			studentResponses.add(sr);
		}
		
		return studentResponses;
	}

	@Override
	public List<StudentRoundStatusResponse> getAllRoundsByStudentIdAndDriveId(Long studentId, Long driveId) {
		Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
		List<StudentRoundStatusResponse> response = new ArrayList<>();
		for(StudentRoundStatus srs : student.getRoundStatuses()) {
			if (!srs.getDrive().getId().equals(driveId)) continue;
			StudentRoundStatusResponse srsr = new StudentRoundStatusResponse();
			srsr.setRoundName(srs.getRoundName());
			srsr.setRoundNumber(srs.getRoundNumber());
			srsr.setStatus(srs.getStatus());
			srsr.setId(srs.getId());
			response.add(srsr);
		}
		return response;
	}

	@Override
	public Drive getDrivesByStudentId(Long studentId) {
		Student student = studentRepository.findById(studentId).orElseThrow(()-> new RuntimeException("Student not found"));
		Drive drive = student.getDrive();
		return drive;
	}
}
