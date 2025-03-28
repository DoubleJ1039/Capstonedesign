package com.example.pas.repositories;

import com.example.pas.models.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * 문제 데이터 접근을 위한 리포지토리 인터페이스
 * MongoDB와의 데이터 연동을 담당
 * 문제의 생성, 조회, 수정, 삭제 기능을 제공
 */
public interface QuestionRepository extends MongoRepository<Question, String> {
    /**
     * 특정 방의 모든 문제를 조회
     * @param roomCode 방 코드
     * @return 해당 방의 문제 목록
     */
    List<Question> findByRoomCode(String roomCode);

    /**
     * 특정 방의 모든 문제를 삭제
     * 방이 삭제될 때 해당 방의 모든 문제도 함께 삭제
     * @param roomCode 방 코드
     */
    void deleteByRoomCode(String roomCode);
} 