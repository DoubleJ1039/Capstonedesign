package com.example.pas.controllers;

import com.example.pas.models.Board;
import com.example.pas.repositories.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 게시판 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 게시글의 CRUD 작업을 처리
 */
@RestController
@RequestMapping("/api/boards")
public class BoardController {
    
    @Autowired
    private BoardRepository boardRepository;
    
    /**
     * 새로운 게시글을 생성
     * @param board 생성할 게시글 정보
     * @return 생성된 게시글
     */
    @PostMapping
    public Board createBoard(@RequestBody Board board) {
        return boardRepository.save(board);
    }
    
    /**
     * 특정 방의 모든 게시글을 조회
     * @param roomCode 방 코드
     * @return 해당 방의 게시글 목록
     */
    @GetMapping("/room/{roomCode}")
    public List<Board> getBoardsByRoomCode(@PathVariable String roomCode) {
        return boardRepository.findByRoomCode(roomCode);
    }
    
    /**
     * 특정 게시글을 ID로 조회
     * @param id 게시글 ID
     * @return 조회된 게시글 또는 404 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable String id) {
        return boardRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 특정 게시글을 수정
     * @param id 수정할 게시글 ID
     * @param boardDetails 수정할 내용
     * @return 수정된 게시글 또는 404 응답
     */
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable String id, @RequestBody Board boardDetails) {
        return boardRepository.findById(id)
                .map(board -> {
                    board.setTitle(boardDetails.getTitle());
                    board.setContent(boardDetails.getContent());
                    return ResponseEntity.ok(boardRepository.save(board));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 특정 게시글을 삭제
     * @param id 삭제할 게시글 ID
     * @return 성공 시 200 OK, 실패 시 404 응답
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable String id) {
        return boardRepository.findById(id)
                .map(board -> {
                    boardRepository.delete(board);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 