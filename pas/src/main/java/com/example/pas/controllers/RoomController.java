package com.example.pas.controllers;

import com.example.pas.models.Room;
import com.example.pas.repositories.RoomRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin("*")
public class RoomController {
    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // 방 생성
    @PostMapping("/create")
    public Map<String, Object> createRoom(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String password = request.get("password");
        String professorEmail = request.get("professorEmail");
        String imageBase64 = request.getOrDefault("imageBase64", "");
        String code = generateRoomCode();

        Room room = new Room(name, code, password, professorEmail, imageBase64);
        roomRepository.save(room);

        return Map.of(
                "success", true,
                "message", "방 생성 완료!",
                "roomCode", code);
    }

    // 방 이미지 업데이트
    @PutMapping("/updateImage/{code}")
    public Map<String, Object> updateRoomImage(@PathVariable String code,
            @RequestBody Map<String, String> request) {
        Optional<Room> optionalRoom = roomRepository.findByCode(code);
        if (optionalRoom.isEmpty()) {
            return Map.of("success", false, "message", "방을 찾을 수 없습니다.");
        }

        String base64 = request.get("imageBase64");

        if (base64 == null || base64.isEmpty()) {
            return Map.of("success", false, "message", "이미지가 전달되지 않았습니다.");
        }

        // 길이 제한
        int maxLength = 1_500_000;
        if (base64.length() > maxLength) {
            return Map.of("success", false, "message", "이미지 용량이 너무 큽니다 (1MB 이하만 허용).");
        }

        if (!base64.matches("^[A-Za-z0-9+/=]+$")) {
            return Map.of("success", false, "message", "잘못된 Base64 형식입니다.");
        }

        try {
            Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return Map.of("success", false, "message", "Base64 디코딩 실패: 유효하지 않은 이미지입니다.");
        }

        Room room = optionalRoom.get();
        room.setImageBase64(base64);
        roomRepository.save(room);

        return Map.of("success", true, "message", "이미지가 성공적으로 업로드되었습니다.");
    }

    // 방 참여
    @PostMapping("/join")
    public Map<String, Object> joinRoom(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String password = request.get("password");
        String userId = request.get("userId");
        String nickname = request.get("nickname");

        if (code == null || password == null || userId == null) {
            return Map.of("success", false, "message", "잘못된 요청입니다. 모든 필드를 입력하세요.");
        }

        String emailKey = userId.replace(".", "_");

        Optional<Room> roomOptional = roomRepository.findByCode(code);
        if (roomOptional.isEmpty()) {
            return Map.of("success", false, "message", "방을 찾을 수 없습니다.");
        }
        Room room = roomOptional.get();

        if (!room.getPassword().equals(password)) {
            return Map.of("success", false, "message", "비밀번호가 틀렸습니다.");
        }

        Map<String, String> participants = room.getParticipants();
        if (participants == null) {
            participants = new HashMap<>();
        }

        if (participants.containsKey(emailKey)) {
            String existingNickname = participants.get(emailKey);
            if (existingNickname != null && !existingNickname.isEmpty()) {
                return Map.of("success", true, "message", "기존 닉네임을 불러왔습니다.", "nickname", existingNickname);
            } else {
                if (nickname != null && !nickname.trim().isEmpty()) {
                    if (participants.containsValue(nickname)) {
                        return Map.of("success", false, "message", "이미 사용 중인 닉네임입니다.");
                    }
                    participants.put(emailKey, nickname);
                    room.setParticipants(participants);
                    roomRepository.save(room);
                    return Map.of("success", true, "message", "닉네임이 업데이트되었습니다.", "nickname", nickname);
                } else {
                    return Map.of("success", true, "message", "이미 참여한 사용자입니다. 닉네임을 설정하세요.");
                }
            }
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            participants.put(emailKey, "");
            room.setParticipants(participants);
            roomRepository.save(room);
            return Map.of("success", true, "message", "방에 참여하였습니다. 닉네임을 설정하세요.");
        }

        if (participants.containsValue(nickname)) {
            return Map.of("success", false, "message", "이미 사용 중인 닉네임입니다.");
        }

        participants.put(emailKey, nickname);
        room.setParticipants(participants);
        roomRepository.save(room);

        return Map.of("success", true, "message", "방에 참여하였습니다.", "nickname", nickname);
    }

    // 방 삭제
    @DeleteMapping("/delete/{code}")
    public Map<String, Object> deleteRoom(@PathVariable String code) {
        Optional<Room> roomOptional = roomRepository.findByCode(code);
        if (roomOptional.isPresent()) {
            roomRepository.delete(roomOptional.get());
            return Map.of("success", true, "message", "방이 삭제되었습니다.");
        }
        return Map.of("success", false, "message", "방을 찾을 수 없습니다.");
    }

    // 방 목록 조회
    @GetMapping("/list")
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    // 방 정보 조회
    @GetMapping("/info")
    public Map<String, Object> getRoomInfo(@RequestParam String code) {
        Optional<Room> roomOptional = roomRepository.findByCode(code);
        if (roomOptional.isEmpty()) {
            return Map.of("success", false, "message", "방을 찾을 수 없습니다.");
        }

        Room room = roomOptional.get();
        return Map.of(
                "success", true,
                "name", room.getName(),
                "participants", room.getParticipants(),
                "imageBase64", room.getImageBase64());
    }

    // 방 비밀번호 확인
    @PostMapping("/checkPassword")
    public Map<String, Object> checkRoomPassword(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String password = request.get("password");

        if (code == null || password == null) {
            return Map.of("success", false, "message", "방 코드와 비밀번호를 입력하세요.");
        }

        Optional<Room> roomOptional = roomRepository.findByCode(code);
        if (roomOptional.isEmpty()) {
            return Map.of("success", false, "message", "방을 찾을 수 없습니다.");
        }

        Room room = roomOptional.get();
        if (!room.getPassword().equals(password)) {
            return Map.of("success", false, "message", "비밀번호가 틀렸습니다.");
        }

        return Map.of("success", true, "message", "비밀번호 확인 완료");
    }

    // 방 코드 생성
    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
