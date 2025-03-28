package com.example.pas.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 문제를 표현하는 모델 클래스
 * MongoDB의 questions 컬렉션에 매핑됨
 */
@Document(collection = "questions")
public class Question {
    @Id
    private String id;          // 문제 고유 ID
    private String roomCode;    // 문제가 속한 방 코드
    private String title;       // 문제 제목
    private String content;     // 문제 내용
    private String answer;      // 정답
    private int score;          // 문제 배점
    private String createdBy;   // 문제 출제자
    private LocalDateTime createdAt;  // 생성 시간
    private List<Submission> submissions;  // 제출된 답안 목록
    private boolean isObjective;  // 객관식 여부
    private List<String> options;  // 객관식 보기 목록

    /**
     * 답안 제출 정보를 담는 내부 클래스
     * 각 사용자의 답안 제출 기록을 저장
     */
    public static class Submission {
        private String userId;      // 제출한 사용자 ID
        private String answer;      // 제출한 답안
        private int score;          // 획득한 점수
        private LocalDateTime submittedAt;  // 제출 시간

        /**
         * 답안 제출 정보 생성자
         * @param userId 제출한 사용자 ID
         * @param answer 제출한 답안
         */
        public Submission(String userId, String answer) {
            this.userId = userId;
            this.answer = answer;
            this.submittedAt = LocalDateTime.now();
        }

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public LocalDateTime getSubmittedAt() { return submittedAt; }
        public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    }

    /**
     * 기본 생성자
     * 생성 시 현재 시간으로 작성 시간을 설정
     */
    public Question() {
        this.createdAt = LocalDateTime.now();
        this.submissions = new ArrayList<>();
        this.options = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<Submission> getSubmissions() { return submissions; }
    public void setSubmissions(List<Submission> submissions) { this.submissions = submissions; }
    public boolean isObjective() { return isObjective; }
    public void setObjective(boolean objective) { isObjective = objective; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
} 