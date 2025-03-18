const API_URL = "/api";

document.addEventListener("DOMContentLoaded", () => {
    const authButton = document.getElementById("authButton");
    const userGreeting = document.getElementById("userGreeting");

    // 로컬 스토리지에서 로그인 정보 확인
    const loggedInUser = localStorage.getItem("loggedInUser");

    if (loggedInUser) {
        authButton.textContent = "로그아웃";
        authButton.className = "logout-btn";
        userGreeting.textContent = `안녕하세요 ${loggedInUser} 님`;
    } else {
        authButton.textContent = "로그인";
        authButton.className = "login-btn";
        userGreeting.textContent = ""; // 로그인 안 되어 있으면 표시 X
    }

    loadRooms(); // 페이지 로드 시 방 목록 불러오기
});

// 로그인 / 로그아웃 기능
function handleAuth() {
    const loggedInUser = localStorage.getItem("loggedInUser");

    if (loggedInUser) {
        // 로그아웃 처리
        localStorage.removeItem("loggedInUser");
        alert("로그아웃되었습니다.");
        location.reload(); // 페이지 새로고침
    } else {
        // 로그인 페이지로 이동
        window.location.href = "login.html";
    }
}

// 방 참여 기능
function joinRoom() {
    const roomCode = document.getElementById("room-code").value;
    if (!roomCode) {
        alert("방 코드를 입력하세요!");
        return;
    }
    alert(`방 ${roomCode}에 참여합니다!`);
}

// 방 목록 불러오기
function loadRooms() {
    fetch(`${API_URL}/rooms/list`)
        .then(response => response.json())
        .then(data => {
            const roomsList = document.getElementById("rooms");
            roomsList.innerHTML = "";
            data.forEach(room => {
                const li = document.createElement("li");
                li.textContent = `방: ${room.name} (ID: ${room.id})`;
                roomsList.appendChild(li);
            });
        })
        .catch(error => console.error("방 목록 불러오기 오류:", error));
}
