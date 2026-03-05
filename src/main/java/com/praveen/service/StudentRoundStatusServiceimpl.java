package com.praveen.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.praveen.dto.BulkUpdateResponse;
import com.praveen.dto.UpdateStudentRoundStatusRequest;
import com.praveen.entities.Student;
import com.praveen.entities.StudentDrive;
import com.praveen.entities.StudentRoundStatus;
import com.praveen.repository.StudentRepository;
import com.praveen.repository.StudentRoundStatusRepository;

@Service
public class StudentRoundStatusServiceimpl implements StudentRoundStatusService {

    @Autowired
    private StudentRoundStatusRepository roundRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Override
    public void updateStatus(Long id, UpdateStudentRoundStatusRequest request) {

        StudentRoundStatus entity = roundRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Student round status not found"));

        entity.setStatus(request.getStatus());
        roundRepo.save(entity);
        
        int originalRoundNumber = entity.getRoundNumber();
        
        if("REJECTED".equals(request.getStatus())) {
        	StudentDrive sd = entity.getStudentDrive();
        	for(StudentRoundStatus srs : sd.getStudentRoundStatuses()) {
        		if(srs.getRoundNumber() > originalRoundNumber) {
        			srs.setStatus("REJECTED");
        			roundRepo.save(srs);
        		}
        	}
        }
    }

    @Override
    public BulkUpdateResponse bulkUpdateStatus(
            MultipartFile file,
            Long driveId) throws Exception {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        int totalRows = 0;
        int successCount = 0;
        List<String> failedMessages = new ArrayList<>();

        for (Row row : sheet) {

            if (row.getRowNum() == 0) continue; // Skip header

            totalRows++;

            try {

                String email = row.getCell(0).getStringCellValue().trim();
                int roundNumber = (int) row.getCell(1).getNumericCellValue();
                String status = row.getCell(2).getStringCellValue().trim().toUpperCase();

                if (!status.equals("SELECTED") &&
                    !status.equals("REJECTED") &&
                    !status.equals("IN PROGRESS")) {
                    throw new RuntimeException("Invalid status value");
                }

                // 1️⃣ Find Student
                Student student = studentRepo.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("Student not found"));

                // 2️⃣ Find Correct StudentDrive
                StudentDrive correctDrive = student.getStudentDrives()
                        .stream()
                        .filter(sd -> sd.getDrive().getId().equals(driveId))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException("Student not registered for this drive"));

                // 3️⃣ Find Correct Round
                StudentRoundStatus round = correctDrive.getStudentRoundStatuses()
                        .stream()
                        .filter(r -> r.getRoundNumber() == roundNumber)
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException("Round not found"));

                // 4️⃣ Update Status
                round.setStatus(status);
                roundRepo.save(round);
                
                int originalRoundNumber = round.getRoundNumber();
                if("REJECTED".equals(status)) {
                	StudentDrive sd = round.getStudentDrive();
                	for(StudentRoundStatus srs : sd.getStudentRoundStatuses()) {
                		if(srs.getRoundNumber() > originalRoundNumber) {
                			srs.setStatus("REJECTED");
                			roundRepo.save(srs);
                		}
                	}
                }

                successCount++;

            } catch (Exception e) {

                failedMessages.add(
                        "Row " + (row.getRowNum() + 1) +
                        ": " + e.getMessage()
                );
            }
        }

        workbook.close();

        return new BulkUpdateResponse(
                totalRows,
                successCount,
                failedMessages.size(),
                failedMessages
        );
    }
}