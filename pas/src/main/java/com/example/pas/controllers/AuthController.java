package com.example.pas.controllers;

import com.example.pas.models.User;
import com.example.pas.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        String email = user.get("email");
        String password = user.get("password");

        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            response.put("success", false);
            response.put("message", "이미 등록된 이메일입니다.");
            return ResponseEntity.badRequest().body(response);
        }

        User newUser = new User(email, password);
        userRepository.save(newUser);

        response.put("success", true);
        response.put("message", "회원가입 성공!");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        String email = user.get("email");
        String password = user.get("password");

        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isPresent() && password.equals(foundUser.get().getPassword())) {
            response.put("success", true);
            response.put("message", "로그인 성공!");
        } else {
            response.put("success", false);
            response.put("message", "이메일 또는 비밀번호가 틀립니다.");
        }
        return ResponseEntity.ok(response);
    }
}
