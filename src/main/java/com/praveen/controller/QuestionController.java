package com.praveen.controller;

import com.praveen.entities.Question;
import com.praveen.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@CrossOrigin
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/save")
    public Question saveQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question);
    }

    @GetMapping("/getAll")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }
    
    @PostMapping("/upload")
    public String uploadQuestions(@RequestParam("file") MultipartFile file) {
        questionService.saveQuestionsFromExcel(file);
        return "Questions uploaded successfully";
    }
}