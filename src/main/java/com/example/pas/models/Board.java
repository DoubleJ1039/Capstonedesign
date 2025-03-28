package com.example.pas.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * 게시판 게시글을 표현하는 모델 클래스
 * MongoDB의 boards 컬렉션에 매핑됨
 */
@Document(collection = "boards")
public class Board {
    @Id
    private String id;          // 게시글 고유 ID
    private String roomCode;    // 게시글이 속한 방 코드
    private String title;       // 게시글 제목
    private String content;     // 게시글 내용
    private String author;      // 작성자
    private LocalDateTime createdAt;  // 작성 시간
    
    /**
     * 기본 생성자
     * 생성 시 현재 시간으로 작성 시간을 설정
     */
    public Board() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRoomCode() {
        return roomCode;
    }
    
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 