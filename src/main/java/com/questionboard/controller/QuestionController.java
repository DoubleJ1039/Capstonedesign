package com.questionboard.controller;

import com.questionboard.model.Question;
import com.questionboard.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 질문 게시판의 REST API를 처리하는 컨트롤러
 * 질문의 생성, 조회, 삭제 등의 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:8080")
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * 모든 질문 목록을 조회합니다.
     * @return 질문 목록
     */
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    /**
     * 새로운 질문을 생성합니다.
     * 생성 시간과 수정 시간을 자동으로 설정합니다.
     * @param question 생성할 질문 정보
     * @return 생성된 질문
     */
    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        return questionRepository.save(question);
    }

    /**
     * ID로 특정 질문을 조회합니다.
     * @param id 조회할 질문의 ID
     * @return 조회된 질문, 없을 경우 null
     */
    @GetMapping("/{id}")
    public Question getQuestion(@PathVariable String id) {
        return questionRepository.findById(id).orElse(null);
    }

    /**
     * ID로 특정 질문을 삭제합니다.
     * @param id 삭제할 질문의 ID
     */
    @DeleteMapping("/{id}")
    public void deleteQuestion(@PathVariable String id) {
        questionRepository.deleteById(id);
    }
} 