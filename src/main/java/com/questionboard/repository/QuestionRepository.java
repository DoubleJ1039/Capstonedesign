package com.questionboard.repository;

import com.questionboard.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 질문 데이터에 대한 데이터베이스 작업을 처리하는 리포지토리 인터페이스
 * MongoDB를 사용하여 질문 데이터의 CRUD 작업을 수행합니다.
 */
public interface QuestionRepository extends MongoRepository<Question, String> {
} 