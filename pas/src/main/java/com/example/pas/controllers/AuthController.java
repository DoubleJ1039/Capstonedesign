package com.example.pas.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private Map<String, String> userDatabase = new HashMap<>();

    // 일반 회원가입
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        String email = user.get("email");
        String password = user.get("password");

        if (userDatabase.containsKey(email)) {
            response.put("success", false);
            response.put("message", "이미 등록된 이메일입니다.");
        } else {
            userDatabase.put(email, password);
            response.put("success", true);
            response.put("message", "회원가입 성공!");
        }
        return response;
    }

    // 일반 로그인
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> user) {
        Map<String, Object> response = new HashMap<>();
        String email = user.get("email");
        String password = user.get("password");

        if (userDatabase.containsKey(email) && userDatabase.get(email).equals(password)) {
            response.put("success", true);
            response.put("message", "로그인 성공!");
        } else {
            response.put("success", false);
            response.put("message", "이메일 또는 비밀번호가 틀립니다.");
        }
        return response;
    }

    // 카카오 로그인
    private final String KAKAO_CLIENT_ID = "카카오 REST API 키";
    private final String KAKAO_REDIRECT_URI = "http://localhost:8080/api/auth/kakao/callback";

    @GetMapping("/kakao/login")
    public ResponseEntity<Void> kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?client_id=" + KAKAO_CLIENT_ID +
                "&redirect_uri=" + KAKAO_REDIRECT_URI +
                "&response_type=code";
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create(kakaoAuthUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // 카카오 로그인 콜백
    @GetMapping("/kakao/callback")
    public ResponseEntity<String> kakaoCallback(@RequestParam("code") String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=authorization_code" +
                "&client_id=" + KAKAO_CLIENT_ID +
                "&redirect_uri=" + KAKAO_REDIRECT_URI +
                "&code=" + code;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        return ResponseEntity.ok(response.getBody().toString());
    }

    // 카카오 사용자 정보 가져오기
    @GetMapping("/kakao/userinfo")
    public ResponseEntity<String> getUserInfo(@RequestParam("access_token") String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);
        return ResponseEntity.ok(response.getBody().toString());
    }
}
