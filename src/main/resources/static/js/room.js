const API_URL = "/api/rooms";

const urlParams = new URLSearchParams(window.location.search);
const roomCode = urlParams.get("code");
if (!roomCode) {
    alert("방 코드가 없습니다!");
    window.location.href = "index.html";
}

const roomPassword = localStorage.getItem(`roomPassword_${roomCode}`);
if (!roomPassword) {
    alert("비밀번호 인증이 필요합니다.");
    window.location.href = "index.html";
}

let userId = localStorage.getItem("loggedInUser");
if (!userId) {
    alert("로그인 정보가 없습니다. 다시 로그인하세요.");
    window.location.href = "login.html";
    throw new Error("No loggedInUser found.");
}

function loadRoomInfo() {
    fetch(`${API_URL}/info?code=${roomCode}`)
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                alert("방을 찾을 수 없습니다!");
                window.location.href = "index.html";
                return;
            }

            document.getElementById("room-title").textContent = `방: ${data.name} (코드: ${roomCode})`;

            updateParticipants(data.participants);
            const emailKey = userId.replace(/\./g, "_");
            if (data.participants[emailKey] && data.participants[emailKey].length > 0) {
                nickname = data.participants[emailKey];
                startChat();
            } else {
                document.getElementById("nickname-section").style.display = "block";
            }
        })
        .catch(error => console.error("방 정보 불러오기 오류:", error));
}

//참여자 목록
function updateParticipants(participants) {
    const participantList = document.getElementById("participant-list");
    participantList.innerHTML = "";

    let count = 0;
    for (const [email, name] of Object.entries(participants)) {
        const li = document.createElement("li");
        li.textContent = name || "(닉네임 미설정)";
        participantList.appendChild(li);
        count++;
    }
    document.getElementById("participant-count").textContent = `${count}명`;
}


//닉네임 설정
function setNickname() {
    const input = document.getElementById("nickname-input").value.trim();
    if (!input) {
        alert("닉네임을 입력하세요!");
        return;
    }

    fetch(`${API_URL}/join`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            code: roomCode,
            password: roomPassword,
            userId: userId,
            nickname: input
        })
    })
    .then(response => response.json())
    .then(data => {
        if (!data.success) {
            alert(data.message);
        } else {
            nickname = input;
            localStorage.setItem(`nickname_${roomCode}`, nickname);
            startChat();
        }
    })
    .catch(error => console.error("닉네임 설정 오류:", error));
}

//채팅 시작
function startChat() {
    document.getElementById("nickname-section").style.display = "none";
    document.getElementById("room-info").style.display = "block";
    document.getElementById("chat").style.display = "block";
    loadRoomInfo();
}

//메시지 전송
function sendMessage() {
    const messageInput = document.getElementById("chat-message");
    const message = messageInput.value.trim();
    if (!message) return;

    const chatBox = document.getElementById("chat-box");
    const newMessage = document.createElement("p");
    newMessage.textContent = `${nickname}: ${message}`;
    chatBox.appendChild(newMessage);
    
    messageInput.value = "";
}

//방 나가기
function leaveRoom() {
    localStorage.removeItem(`roomPassword_${roomCode}`);
    alert("방을 나갑니다.");
    window.location.href = "index.html";
}

loadRoomInfo();
