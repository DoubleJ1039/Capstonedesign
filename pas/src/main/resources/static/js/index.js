const API_URL = "/api";

document.addEventListener("DOMContentLoaded", () => {
    const authButton = document.getElementById("authButton");
    const userGreeting = document.getElementById("userGreeting");

    const loggedInUser = localStorage.getItem("loggedInUser");

    if (loggedInUser) {
        authButton.textContent = "로그아웃";
        authButton.className = "logout-btn";
        userGreeting.textContent = `안녕하세요 ${loggedInUser} 님`;
    } else {
        authButton.textContent = "로그인";
        authButton.className = "login-btn";
        userGreeting.textContent = "";
    }

    loadRooms();
});

function handleAuth() {
    const loggedInUser = localStorage.getItem("loggedInUser");

    if (loggedInUser) {
        localStorage.removeItem("loggedInUser");
        alert("로그아웃되었습니다.");
        location.reload();
    } else {
        window.location.href = "login.html";
    }
}

function joinRoom() {
    const roomCode = document.getElementById("room-code").value;
    if (!roomCode) {
        alert("방 코드를 입력하세요!");
        return;
    }
    alert(`방 ${roomCode}에 참여합니다!`);
}

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
