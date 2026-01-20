
package com.praveen.controller;

import com.praveen.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/springApi/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/upload")
    public String uploadStudents(@RequestParam Long driveId,
                                 @RequestParam("file") MultipartFile file) {
        studentService.uploadStudentsForDrive(driveId, file);
        return "Students uploaded successfully";
    }
}
