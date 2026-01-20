package com.praveen.service;

import com.praveen.entities.*;
import com.praveen.repository.*;
import com.praveen.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;

import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {

    private final DriveRepository driveRepository;

    public StudentServiceImpl(DriveRepository driveRepository) {
        this.driveRepository = driveRepository;
    }

    @Override
    public void uploadStudentsForDrive(Long driveId, MultipartFile file) {

        Drive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new RuntimeException("Drive not found"));

        List<Student> parsedStudents = parseExcel(file);

        for (Student s : parsedStudents) {
            s.setDrive(drive);                 // owning side
            drive.getStudents().add(s);        // inverse side
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
}
