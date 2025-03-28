package com.example.pas.controllers;

import com.example.pas.models.Question;
import com.example.pas.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 문제 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 문제의 CRUD 작업과 답안 제출, 점수 부여를 처리
 * 관리자와 사용자의 문제 관련 모든 요청을 처리
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    /**
     * 새로운 문제를 생성
     * 관리자가 새로운 문제를 출제할 때 사용
     * @param question 생성할 문제 정보 (제목, 내용, 정답, 배점 등)
     * @return 생성된 문제
     */
    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return questionRepository.save(question);
    }
    
    /**
     * 특정 방의 모든 문제를 조회
     * 방에 입장한 사용자들이 문제 목록을 볼 때 사용
     * @param roomCode 방 코드
     * @return 해당 방의 문제 목록
     */
    @GetMapping("/room/{roomCode}")
    public List<Question> getQuestionsByRoomCode(@PathVariable String roomCode) {
        List<Question> questions = questionRepository.findByRoomCode(roomCode);
        // 객관식 문제의 경우 options가 null이면 빈 리스트로 초기화
        questions.forEach(question -> {
            if (question.isObjective() && question.getOptions() == null) {
                question.setOptions(new ArrayList<>());
            }
        });
        return questions;
    }
    
    /**
     * 특정 문제를 ID로 조회
     * 문제의 상세 정보를 볼 때 사용
     * @param id 문제 ID
     * @return 조회된 문제 또는 404 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable String id) {
        return questionRepository.findById(id)
                .map(question -> {
                    if (question.isObjective() && question.getOptions() == null) {
                        question.setOptions(new ArrayList<>());
                    }
                    return ResponseEntity.ok(question);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 답안을 제출하고 점수를 부여
     * 사용자가 답안을 제출하고 자동으로 점수가 계산됨
     * @param id 문제 ID
     * @param submission 제출 정보 (userId, answer)
     * @return 수정된 문제 또는 404 응답
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<Question> submitAnswer(
            @PathVariable String id,
            @RequestBody Map<String, String> submission) {
        return questionRepository.findById(id)
                .map(question -> {
                    // 새로운 답안 제출 정보 생성
                    Question.Submission newSubmission = new Question.Submission(
                            submission.get("userId"),
                            submission.get("answer")
                    );
                    
                    // 정답과 비교하여 점수 부여
                    if (submission.get("answer").equals(question.getAnswer())) {
                        newSubmission.setScore(question.getScore());
                    } else {
                        newSubmission.setScore(0);
                    }
                    
                    // 답안 제출 정보를 문제에 추가
                    question.getSubmissions().add(newSubmission);
                    return ResponseEntity.ok(questionRepository.save(question));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 특정 문제를 삭제
     * 관리자가 문제를 삭제할 때 사용
     * @param id 삭제할 문제 ID
     * @return 성공 시 200 OK, 실패 시 404 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String id) {
        return questionRepository.findById(id)
                .map(question -> {
                    questionRepository.delete(question);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 