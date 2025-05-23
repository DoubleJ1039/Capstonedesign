package com.example.pas.controllers;

import com.example.pas.models.Room;
import com.example.pas.models.User;
import com.example.pas.repositories.RoomRepository;
import com.example.pas.repositories.UserRepository;
import com.example.pas.models.CloudinaryService;
import com.example.pas.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.apache.commons.lang3.tuple.Pair;
import java.util.concurrent.ConcurrentHashMap;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CloudinaryService cloudinaryService;

    private final Map<String, Pair<String, Long>> emailCodeStorage = new ConcurrentHashMap<>();

    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestParam String email) {
        Optional<User> user = userRepository.findByEmail(email);
        Map<String, Object> response = new HashMap<>();

        if (user.isPresent()) {
            response.put("success", true);
            response.put("email", user.get().getEmail());
            response.put("displayName", user.get().getDisplayName());
        } else {
            response.put("success", false);
            response.put("message", "사용자 정보를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        String email = user.get("email");
        String password = user.get("password");
        String displayName = user.get("displayName");

        if (email == null || password == null || displayName == null) {
            response.put("success", false);
            response.put("message", "필수 항목이 누락되었습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            response.put("success", false);
            response.put("message", "이미 등록된 이메일입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        String encodedPassword = passwordEncoder.encode(password);
        User newUser = new User(email, encodedPassword, displayName);

        userRepository.save(newUser);

        response.put("success", true);
        response.put("message", "회원가입 성공!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        String email = user.get("email");
        String rawPassword = user.get("password");

        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isPresent()) {
            String hashedPassword = foundUser.get().getPassword();
            if (passwordEncoder.matches(rawPassword, hashedPassword)) {
                response.put("success", true);
                response.put("message", "로그인 성공!");
                return ResponseEntity.ok(response);
            }
        }

        response.put("success", false);
        response.put("message", "이메일 또는 비밀번호가 틀립니다.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");

        if (email == null || !email.contains("@")) {
            response.put("exists", false);
            response.put("message", "이메일 형식이 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean exists = userRepository.existsByEmail(email);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    private final Map<String, Long> lastSentMap = new ConcurrentHashMap<>();

    @PostMapping("/send-email-code")
    public ResponseEntity<Map<String, Object>> sendEmailCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");

        long now = System.currentTimeMillis();
        long lastSent = lastSentMap.getOrDefault(email, 0L);
        if (now - lastSent < 60_000) {
            response.put("success", false);
            response.put("message", "1분 후에 다시 시도해 주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        String code = generateCode();
        emailCodeStorage.put(email, Pair.of(code, now));
        emailService.sendVerificationCode(email, code);

        lastSentMap.put(email, now);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, Object>> verifyEmailCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");
        String code = request.get("code");

        Pair<String, Long> entry = emailCodeStorage.get(email);
        long now = System.currentTimeMillis();

        boolean verified = entry != null &&
                entry.getLeft().equals(code) &&
                now - entry.getRight() <= 5 * 60 * 1000;

        response.put("verified", verified);
        return ResponseEntity.ok(response);
    }

    // 비밀번호 확인
    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String password = req.get("password");

        Optional<User> user = userRepository.findByEmail(email);
        Map<String, Object> response = new HashMap<>();

        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            response.put("valid", true);
        } else {
            response.put("valid", false);
        }

        return ResponseEntity.ok(response);
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String email = request.get("email");
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            response.put("success", false);
            response.put("message", "현재 비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        String hashedNewPw = passwordEncoder.encode(newPassword);
        user.setPassword(hashedNewPw);
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 비밀번호 초기화
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String newPassword = req.get("newPassword");

        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userOpt.get();
        String hashedNewPw = passwordEncoder.encode(newPassword);
        user.setPassword(hashedNewPw);
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "비밀번호가 성공적으로 재설정되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 닉네임 검사
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNickname(@RequestParam String name) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = userRepository.existsByDisplayName(name);
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // 닉네임 변경
    @PostMapping("/change-nickname")
    public ResponseEntity<Map<String, Object>> changeNickname(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");
        String newNickname = request.get("newNickname");

        if (newNickname == null || newNickname.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "닉네임을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean exists = userRepository.existsByDisplayName(newNickname);
        if (exists) {
            response.put("success", false);
            response.put("message", "이미 사용 중인 닉네임입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userOpt.get();
        user.setDisplayName(newNickname);
        userRepository.save(user);

        response.put("success", true);
        response.put("message", "닉네임이 성공적으로 변경되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 회원탈퇴
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "사용자를 찾을 수 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Room> userRooms = roomRepository.findByProfessorEmail(email);
        for (Room room : userRooms) {
            String publicId = room.getImagePublicId();
            if (publicId != null && !publicId.isBlank()) {
                cloudinaryService.deleteImage(publicId);
            }
            roomRepository.delete(room);
        }
        userRepository.deleteById(userOpt.get().getId());
        response.put("success", true);
        response.put("message", "회원 탈퇴 및 이미지, 생성한 방 삭제 완료");
        return ResponseEntity.ok(response);
    }

}