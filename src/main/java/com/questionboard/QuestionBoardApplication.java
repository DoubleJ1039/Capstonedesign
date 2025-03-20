package com.questionboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 질문 게시판 애플리케이션의 메인 클래스
 * Spring Boot 애플리케이션을 시작하는 진입점입니다.
 */
@SpringBootApplication
public class QuestionBoardApplication {
    /**
     * 애플리케이션의 메인 메서드
     * @param args 커맨드 라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(QuestionBoardApplication.class, args);
    }
} 