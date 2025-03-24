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
    private String imageBase64;
    private Map<String, String> participants;
    private List<String> anonymousQuestions;
    private List<que> testQuestions;

    public Room() {
    }

    public Room(String name, String code, String password, String professorEmail, String imageBase64) {
        this.name = name;
        this.code = code;
        this.password = password;
        this.professorEmail = professorEmail;
        this.imageBase64 = imageBase64;
    }

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

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
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
}
