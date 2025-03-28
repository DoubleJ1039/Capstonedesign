package com.example.pas.repositories;

import com.example.pas.models.Board;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

/**
 * 게시판 데이터 접근을 위한 리포지토리 인터페이스
 * MongoDB와의 데이터 연동을 담당
 */
public interface BoardRepository extends MongoRepository<Board, String> {
    /**
     * 특정 방에 속한 모든 게시글을 조회
     * @param roomCode 방 코드
     * @return 해당 방의 게시글 목록
     */
    List<Board> findByRoomCode(String roomCode);

    /**
     * 특정 방의 모든 게시글을 삭제
     * @param roomCode 방 코드
     */
    void deleteByRoomCode(String roomCode);
} 