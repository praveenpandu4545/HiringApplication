package com.praveen.service;

import com.praveen.entities.Question;
import com.praveen.repository.QuestionRepository;
import com.praveen.service.QuestionService;
import lombok.RequiredArgsConstructor;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    
    @Override
    public void saveQuestionsFromExcel(MultipartFile file) {
        List<Question> questions = parseExcelQuestions(file);
        questionRepository.saveAll(questions);
    }
    
    private List<Question> parseExcelQuestions(MultipartFile file) {

        List<Question> questions = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                String questionText = getString(row.getCell(0));
                String option1 = getString(row.getCell(1));
                String option2 = getString(row.getCell(2));
                String option3 = getString(row.getCell(3));
                String option4 = getString(row.getCell(4));
                String correctAnswer = getString(row.getCell(5));
                String domain = getString(row.getCell(6));
                String difficulty = getString(row.getCell(7));

                // 🔥 Validation (VERY IMPORTANT)
                if (questionText.isEmpty() || correctAnswer.isEmpty()) continue;

                List<String> options = List.of(option1, option2, option3, option4);

                // Ensure correctAnswer exists in options
                if (!options.contains(correctAnswer)) {
                    System.out.println("Invalid correct answer at row: " + rowIndex);
                    continue;
                }

                Question question = Question.builder()
                        .questionText(questionText)
                        .options(options)
                        .correctAnswer(correctAnswer)
                        .domain(domain)
                        .difficulty(difficulty)
                        .build();

                questions.add(question);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Excel: " + e.getMessage());
        }

        return questions;
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
	public List<String> getAllDomains() {
		List<Question> questions = questionRepository.findAll();
		HashSet<String> set = new HashSet<>();
		for(Question question : questions) {
			String d = question.getDomain();
			set.add(d);
		}
		List<String> domains = new ArrayList<>();
		for(String s : set) {
			domains.add(s);
		}
		return domains;
	}
}