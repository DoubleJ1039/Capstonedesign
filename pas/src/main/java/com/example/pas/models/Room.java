package com.example.pas.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "rooms")
public class Room {
    @Id
    private String id;
    private String name;
    private String code;
    private String password;
    private String professorEmail;

    private String imageUrl;
    private String imagePublicId;

    private Map<String, String> participants;
    private List<String> anonymousQuestions;
    private Long endTime;
    private Map<Integer, Map<String, Long>> submitTimes;
    private List<que> testQuestions;
    private boolean isStarted = false;
    private int currentQuestionIndex = 0;

    private Map<String, Integer> scores;
    private Map<String, List<Boolean>> answers;

    public Room() {
    }

    public Room(String name, String code, String password, String professorEmail, String imageUrl,
            String imagePublicId) {
        this.name = name;
        this.code = code;
        this.password = password;
        this.professorEmail = professorEmail;
        this.imageUrl = imageUrl;
        this.imagePublicId = imagePublicId;
    }

    // 기본 getter/setter

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getPassword() {
        return password;
    }

    public String getProfessorEmail() {
        return professorEmail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImagePublicId() {
        return imagePublicId;
    }

    public void setImagePublicId(String imagePublicId) {
        this.imagePublicId = imagePublicId;
    }

    public Map<String, String> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, String> participants) {
        this.participants = participants;
    }

    public void addParticipant(String email, String nickname) {
        if (this.participants != null) {
            this.participants.put(email.replace(".", "_"), nickname);
        }
    }

    public List<String> getAnonymousQuestions() {
        return anonymousQuestions;
    }

    public void setAnonymousQuestions(List<String> anonymousQuestions) {
        this.anonymousQuestions = anonymousQuestions;
    }

    public List<que> getTestQuestions() {
        return testQuestions;
    }

    public void setTestQuestions(List<que> testQuestions) {
        this.testQuestions = testQuestions;
    }

    public void addTestQuestion(que question) {
        if (this.testQuestions != null) {
            this.testQuestions.add(question);
        }
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
    }

    public Map<String, List<Boolean>> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, List<Boolean>> answers) {
        this.answers = answers;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Map<Integer, Map<String, Long>> getSubmitTimes() {
        return submitTimes;
    }

    public void setSubmitTimes(Map<Integer, Map<String, Long>> submitTimes) {
        this.submitTimes = submitTimes;
    }
}
