package com.praveen.repository;
import com.praveen.entities.Question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	List<Question> findByDomainIn(List<String> domains);
}
