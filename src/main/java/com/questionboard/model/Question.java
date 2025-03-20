package com.questionboard.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * 질문 게시판의 질문을 나타내는 엔티티 클래스
 * MongoDB에 저장되는 문서 구조를 정의합니다.
 */
@Data
@Document(collection = "questions")
public class Question {
    /**
     * 질문의 고유 식별자
     */
    @Id
    private String id;

    /**
     * 질문의 제목
     */
    private String title;

    /**
     * 질문의 내용
     */
    private String content;

    /**
     * 질문이 생성된 시간
     */
    private LocalDateTime createdAt;

    /**
     * 질문이 마지막으로 수정된 시간
     */
    private LocalDateTime updatedAt;
} 