package com.example.pas.models;

import java.util.List;

public class que {
    private String questionText; // 문제 내용
    private String questionImage; // 문제 이미지
    private List<String> choices; // 보기
    private Object correctAnswer; // 정답
    private String name; // 문제 제목
    private int time; // 제한 시간 (초)
    private int score; // 배점
    private String type; // 문제 유형

    public que() {
    }

    public que(String questionText, String questionImage, List<String> choices,
            Object correctAnswer, String name, int time, int score, String type) {
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.name = name;
        this.time = time;
        this.score = score;
        this.type = type;
    }

    // === Getter & Setter ===
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public Object getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Object correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
