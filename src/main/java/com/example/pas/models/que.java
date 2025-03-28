package com.example.pas.models;

import java.util.List;

public class que {
    private String questionText; // 문제 내용
    private String questionImage; // 문제 이미지 (없으면 null)
    private List<String> choices; // 보기 선택지 (최소 2개 이상)
    private String correctAnswer; // 정답

    public que() {
    }

    public que(String questionText, String questionImage, List<String> choices, String correctAnswer) {
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
