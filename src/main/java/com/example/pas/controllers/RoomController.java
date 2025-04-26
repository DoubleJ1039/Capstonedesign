package com.example.pas.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.pas.models.Room;
import com.example.pas.models.que;
import com.example.pas.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class RoomController {

    private final RoomRepository roomRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public RoomController(RoomRepository roomRepository, Cloudinary cloudinary) {
        this.roomRepository = roomRepository;
        this.cloudinary = cloudinary;
    }

    // 방 생성
    @PostMapping("/create")
    public Map<String, Object> createRoom(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String password = request.get("password");
        String professorEmail = request.get("professorEmail");
        String imageUrl = request.getOrDefault("imageUrl", "");
        String imagePublicId = request.getOrDefault("imagePublicId", "");
        String code = generateRoomCode();

        Room room = new Room(name, code, password, professorEmail, imageUrl, imagePublicId);
        roomRepository.save(room);

        return Map.of("success", true, "message", "방 생성 완료!", "roomCode", code);
    }

    // 방 이미지 업데이트
    @PutMapping("/updateImage/{code}")
    public Map<String, Object> updateRoomImage(@PathVariable String code, @RequestBody Map<String, String> request) {
        Optional<Room> optionalRoom = roomRepository.findByCode(code);
        if (optionalRoom.isEmpty()) {
            return Map.of("success", false, "message", "방을 찾을 수 없습니다.");
        }

        Room room = optionalRoom.get();
        try {
            if (room.getImagePublicId() != null && !room.getImagePublicId().isEmpty()) {
                cloudinary.uploader().destroy(room.getImagePublicId(), ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
        }

        String newUrl = request.get("imageUrl");
        String newPublicId = request.get("imagePublicId");

        room.setImageUrl(newUrl);
        room.setImagePublicId(newPublicId);
        roomRepository.save(room);

        return Map.of("success", true, "message", "이미지 업데이트 완료");
    }

    // 방 삭제
    @DeleteMapping("/delete/{code}")
    public Map<String, Object> deleteRoom(@PathVariable String code) {
        Optional<Room> roomOptional = roomRepository.findByCode(code);
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            try {
                if (room.getImagePublicId() != null && !room.getImagePublicId().isEmpty()) {
                    cloudinary.uploader().destroy(room.getImagePublicId(), ObjectUtils.emptyMap());
                }
            } catch (Exception e) {
            }

            roomRepository.delete(room);
            return Map.of("success", true, "message", "방이 삭제되었습니다.");
        }
        return Map.of("success", false, "message", "방을 찾을 수 없습니다.");
    }

    // 방 목록 조회
    @GetMapping("/list")
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    // 방 상세 정보
    @GetMapping("/info")
    public Map<String, Object> getRoomInfo(@RequestParam String code) {
        Optional<Room> roomOptional = roomRepository.findByCode(code);
        if (roomOptional.isEmpty()) {
            return Map.of("success", false, "message", "방을 찾을 수 없습니다.");
        }

        Room room = roomOptional.get();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("name", room.getName());
        response.put("participants", room.getParticipants());
        response.put("imageUrl", room.getImageUrl());
        response.put("imagePublicId", room.getImagePublicId());
        response.put("testQuestions", room.getTestQuestions() != null ? room.getTestQuestions() : new ArrayList<>());
        response.put("professorEmail", room.getProfessorEmail());
        response.put("currentQuestionIndex", room.getCurrentQuestionIndex());
        response.put("endTime", room.getEndTime());

        return response;
    }

    // 닉네임 등록 + 방 참여
    @PostMapping("/join")
    public Map<String, Object> joinRoom(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String password = request.get("password");
        String userId = request.get("userId");
        String nickname = request.get("nickname");

        if (code == null || password == null || userId == null) {
            return Map.of("success", false, "message", "잘못된 요청입니다.");
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
        if (participants == null)
            participants = new HashMap<>();

        String existingNickname = participants.get(emailKey);
        if (existingNickname != null && !existingNickname.trim().isEmpty()) {
            return Map.of("success", true, "message", "기존 닉네임", "nickname", existingNickname);
        }

        if (nickname != null && !nickname.trim().isEmpty()) {
            if (participants.containsValue(nickname)) {
                return Map.of("success", false, "message", "이미 사용 중인 닉네임입니다.");
            }
            participants.put(emailKey, nickname);
            room.setParticipants(participants);
            roomRepository.save(room);
            return Map.of("success", true, "message", "닉네임 등록", "nickname", nickname);
        }

        if (!participants.containsKey(emailKey)) {
            participants.put(emailKey, "");
            room.setParticipants(participants);
            roomRepository.save(room);
        }

        return Map.of("success", true, "message", "닉네임을 설정해주세요.");
    }

    // 방 비밀번호 확인
    @PostMapping("/checkPassword")
    public Map<String, Object> checkRoomPassword(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        String password = request.get("password");

        Optional<Room> roomOptional = roomRepository.findByCode(code);
        if (roomOptional.isEmpty())
            return Map.of("success", false, "message", "방 없음");

        Room room = roomOptional.get();
        if (!room.getPassword().equals(password))
            return Map.of("success", false, "message", "비밀번호 틀림");

        return Map.of("success", true, "message", "확인 완료");
    }

    // 문제 저장
    @PostMapping("/{code}/questions")
    public Map<String, Object> saveTestQuestions(@PathVariable String code, @RequestBody List<que> questions) {
        Optional<Room> roomOpt = roomRepository.findByCode(code);
        if (roomOpt.isEmpty())
            return Map.of("success", false, "message", "방 없음");

        Room room = roomOpt.get();
        room.setTestQuestions(questions);
        roomRepository.save(room);
        return Map.of("success", true, "message", "문제 저장됨");
    }

    // 퀴즈 상태 제어
    @PutMapping("/start/{code}")
    public Map<String, Object> startQuiz(@PathVariable String code) {
        Optional<Room> roomOpt = roomRepository.findByCode(code);
        if (roomOpt.isEmpty()) {
            return Map.of("success", false, "message", "방 없음");
        }

        Room room = roomOpt.get();
        room.setStarted(true);
        room.setCurrentQuestionIndex(0);

        List<que> questions = room.getTestQuestions();
        if (questions != null && !questions.isEmpty()) {
            int timeLimit = questions.get(0).getTime();
            long endTime = System.currentTimeMillis() + timeLimit * 1000L;
            room.setEndTime(endTime);
        }

        roomRepository.save(room);

        return Map.of("success", true, "message", "퀴즈 시작", "endTime", room.getEndTime());
    }

    @PutMapping("/setStart/{code}")
    public Map<String, Object> setQuizStarted(@PathVariable String code, @RequestBody Map<String, Boolean> request) {
        Optional<Room> roomOpt = roomRepository.findByCode(code);
        if (roomOpt.isEmpty())
            return Map.of("success", false, "message", "방 없음");

        Room room = roomOpt.get();
        boolean start = request.getOrDefault("isStarted", false);
        room.setStarted(start);
        if (!start)
            room.setCurrentQuestionIndex(0);
        roomRepository.save(room);
        return Map.of("success", true, "message", "퀴즈 상태 변경");
    }

    @PutMapping("/updateIndex/{code}")
    public Map<String, Object> updateCurrentIndex(@PathVariable String code,
            @RequestBody Map<String, Integer> request) {
        Optional<Room> roomOpt = roomRepository.findByCode(code);
        if (roomOpt.isEmpty())
            return Map.of("success", false, "message", "방 없음");

        Room room = roomOpt.get();
        int index = request.getOrDefault("currentQuestionIndex", 0);
        room.setCurrentQuestionIndex(index);
        roomRepository.save(room);
        return Map.of("success", true, "message", "인덱스 변경");
    }

    @GetMapping("/status")
    public Map<String, Object> getRoomStatus(@RequestParam String code) {
        Optional<Room> roomOpt = roomRepository.findByCode(code);
        if (roomOpt.isEmpty())
            return Map.of("success", false, "message", "방 없음");

        Room room = roomOpt.get();
        return Map.of(
                "success", true,
                "isStarted", room.isStarted(),
                "currentQuestionIndex", room.getCurrentQuestionIndex());
    }

    // 채점 및 점수 반영
    @PostMapping("/submit/{code}")
    public Map<String, Object> submitAnswer(@PathVariable String code, @RequestBody Map<String, Object> payload) {
        Optional<Room> optionalRoom = roomRepository.findByCode(code);
        if (optionalRoom.isEmpty())
            return Map.of("success", false, "message", "방 없음");

        Room room = optionalRoom.get();
        int currentIndex = room.getCurrentQuestionIndex();
        List<que> questions = room.getTestQuestions();

        if (questions == null || currentIndex >= questions.size()) {
            return Map.of("success", false, "message", "문제 없음");
        }

        que question = questions.get(currentIndex);
        String userId = ((String) payload.get("userId")).replace(".", "_");
        if (userId == null || userId.isBlank())
            return Map.of("success", false, "message", "사용자 없음");

        Object correct = question.getCorrectAnswer();
        if (correct == null)
            return Map.of("success", false, "message", "정답 없음");

        boolean isCorrect = false;

        switch (question.getType()) {
            case "multiple" -> {
                Object rawSelected = payload.get("selectedIndexes");
                if (rawSelected instanceof List<?> selectedRaw) {
                    List<Integer> selectedIndexes = selectedRaw.stream()
                            .map(o -> (o instanceof Integer) ? (Integer) o : Integer.parseInt(o.toString()))
                            .toList();

                    if (correct instanceof List<?> correctList) {
                        List<Integer> correctIndexes = correctList.stream()
                                .map(o -> (o instanceof Integer) ? (Integer) o : Integer.parseInt(o.toString()))
                                .toList();
                        isCorrect = new HashSet<>(correctIndexes).equals(new HashSet<>(selectedIndexes));
                    } else {
                        isCorrect = selectedIndexes.size() == 1 &&
                                selectedIndexes.get(0).toString().equals(correct.toString());
                    }
                }
            }
            case "ox" -> {
                String selected = (String) payload.get("selectedAnswer");
                isCorrect = selected != null && selected.trim().equalsIgnoreCase(correct.toString().trim());
            }
            case "short" -> {
                String shortAnswer = (String) payload.get("shortAnswer");
                isCorrect = shortAnswer != null && shortAnswer.trim().equalsIgnoreCase(correct.toString().trim());
            }
        }

        Map<String, List<Boolean>> answers = room.getAnswers();
        if (answers == null) {
            answers = new HashMap<>();
            room.setAnswers(answers);
        }

        answers.computeIfAbsent(userId, k -> new ArrayList<>());
        List<Boolean> answerList = answers.get(userId);
        while (answerList.size() <= currentIndex)
            answerList.add(false);
        answerList.set(currentIndex, isCorrect);

        Map<String, Integer> scores = room.getScores();
        if (scores == null) {
            scores = new HashMap<>();
            room.setScores(scores);
        }

        int scoreToAdd = isCorrect ? question.getScore() : 0;
        scores.put(userId, scores.getOrDefault(userId, 0) + scoreToAdd);

        roomRepository.save(room);

        return Map.of(
                "success", true,
                "correct", isCorrect,
                "score", scores.get(userId));
    }

    private String generateRoomCode() {
        return UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    // 문제 결과 통계 조회
    @GetMapping("/result/{code}/{questionIndex}")
    public Map<String, Object> getQuestionResult(@PathVariable String code, @PathVariable int questionIndex,
            @RequestParam String userId) {
        Optional<Room> roomOpt = roomRepository.findByCode(code);
        if (roomOpt.isEmpty()) {
            return Map.of("success", false, "message", "방 없음");
        }

        Room room = roomOpt.get();

        // 정답자 수 체크
        int totalParticipants = room.getParticipants().size();
        int correctCount = 0;

        Map<String, List<Boolean>> answers = room.getAnswers();
        if (answers != null) {
            for (List<Boolean> answerList : answers.values()) {
                if (answerList.size() > questionIndex && answerList.get(questionIndex)) {
                    correctCount++;
                }
            }
        }

        int correctRate = (int) ((correctCount / (double) totalParticipants) * 100);

        // 점수 랭킹
        List<Map.Entry<String, Integer>> rankingList = new ArrayList<>();
        if (room.getScores() != null) {
            rankingList.addAll(room.getScores().entrySet());
        }
        rankingList.sort((a, b) -> b.getValue() - a.getValue());

        List<Map<String, Object>> rankingResult = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : rankingList) {
            String nickname = room.getParticipants().get(entry.getKey());
            rankingResult.add(Map.of(
                    "nickname", nickname != null ? nickname : "익명",
                    "score", entry.getValue()));
        }

        // 빠르게 푼 사람
        List<Map<String, Object>> fastestResult = new ArrayList<>();
        if (room.getSubmitTimes() != null && room.getSubmitTimes().containsKey(questionIndex)) {
            List<Map.Entry<String, Long>> submitList = new ArrayList<>(
                    room.getSubmitTimes().get(questionIndex).entrySet());
            submitList.sort(Map.Entry.comparingByValue());

            for (int i = 0; i < Math.min(3, submitList.size()); i++) {
                String nickname = room.getParticipants().get(submitList.get(i).getKey());
                long timeTaken = submitList.get(i).getValue();
                fastestResult.add(Map.of(
                        "nickname", nickname != null ? nickname : "익명",
                        "time", timeTaken / 1000.0));
            }
        }

        return Map.of(
                "success", true,
                "correctRate", correctRate,
                "myScore", room.getScores() != null ? room.getScores().getOrDefault(userId.replace(".", "_"), 0) : 0,
                "ranking", rankingResult,
                "fastest", fastestResult);
    }

    @PutMapping("/reset/{code}")
    public Map<String, Object> resetRoom(@PathVariable String code) {
        Optional<Room> roomOpt = roomRepository.findByCode(code);
        if (roomOpt.isEmpty()) {
            return Map.of("success", false, "message", "방이 존재하지 않습니다.");
        }

        Room room = roomOpt.get();
        room.setParticipants(new HashMap<>());
        room.setScores(new HashMap<>());
        room.setAnswers(new HashMap<>());
        room.setCurrentQuestionIndex(0);
        room.setStarted(false);
        room.setEndTime(0L);

        roomRepository.save(room);

        return Map.of("success", true, "message", "방 데이터가 초기화되었습니다.");
    }

}
