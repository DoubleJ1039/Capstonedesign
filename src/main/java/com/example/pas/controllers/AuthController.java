package com.example.pas.controllers;

import com.example.pas.models.User;
import com.example.pas.repositories.UserRepository;
import com.example.pas.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
    private BCryptPasswordEncoder passwordEncoder;

    private final Map<String, String> emailCodeStorage = new HashMap<>();

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

    @PostMapping("/send-email-code")
    public ResponseEntity<Map<String, Object>> sendEmailCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");

        if (email == null || !email.contains("@")) {
            response.put("success", false);
            response.put("message", "이메일 형식이 올바르지 않습니다.");
            return ResponseEntity.badRequest().body(response);
        }

        String code = generateCode();
        emailCodeStorage.put(email, code);
        emailService.sendVerificationCode(email, code);

        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email-code")
    public ResponseEntity<Map<String, Object>> verifyEmailCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");
        String code = request.get("code");

        boolean verified = code != null && code.equals(emailCodeStorage.get(email));
        response.put("verified", verified);
        return ResponseEntity.ok(response);
    }

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
}