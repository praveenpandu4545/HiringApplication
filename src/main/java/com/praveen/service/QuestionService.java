package com.praveen.service;

import com.praveen.entities.Question;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface QuestionService {

    Question saveQuestion(Question question);
    List<Question> getAllQuestions();
    void saveQuestionsFromExcel(MultipartFile file);
}