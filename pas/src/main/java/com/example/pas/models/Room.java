package com.example.pas.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document(collection = "rooms")
public class Room {
    @Id
    private String id; // 방 고유 ID
    private String name; // 방 이름
    private String code; // 고유 코드 (ABCD)
    private String password; // 방 입장 비밀번호
    private String professorEmail; // 방장(교수) 이메일
    private Map<String, String> participants; // (email -> nickname) 형태로 저장
    private List<String> questions; // 익명 질문 리스트

    public Room() {
    }

    public Room(String name, String code, String password, String professorEmail) {
        this.name = name;
        this.code = code;
        this.password = password;
        this.professorEmail = professorEmail;
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

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }
}
