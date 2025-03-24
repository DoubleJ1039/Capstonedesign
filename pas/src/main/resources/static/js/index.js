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
    const roomCode = document.getElementById("room-code").value.trim();
    const roomPassword = document.getElementById("room-password").value.trim();
    const loggedInUser = localStorage.getItem("loggedInUser");

    if (!roomCode) {
        alert("방 코드를 입력하세요!");
        return;
    }
    if (!roomPassword) {
        alert("방 비밀번호를 입력하세요!");
        return;
    }
    if (!loggedInUser) {
        alert("로그인 후 참여 가능합니다.");
        window.location.href = "login.html";
        return;
    }

    fetch(`${API_URL}/rooms/join`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            code: roomCode,
            password: roomPassword,
            userId: loggedInUser
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert("방에 참여하였습니다!");
            localStorage.setItem(`roomPassword_${roomCode}`, roomPassword);
            window.location.href = `room.html?code=${roomCode}`;
        } else {
            alert("방 참여 실패: " + data.message);
        }
    })
    .catch(error => console.error("방 참여 오류:", error));
}

function loadRooms() {
    fetch(`${API_URL}/rooms/list`)
        .then(response => response.json())
        .then(data => {
            const roomsList = document.getElementById("rooms");
            roomsList.innerHTML = "";

            if (data.length === 0) {
                roomsList.innerHTML = "<li>방이 존재하지 않습니다.</li>";
                return;
            }

            data.forEach(room => {
                const li = document.createElement("li");
                li.textContent = `방 이름: ${room.name} / 코드: ${room.code}`;
                roomsList.appendChild(li);
            });
        })
        .catch(error => console.error("방 목록 불러오기 오류:", error));
}


function joinRoomWithCode(code) {
    document.getElementById("room-code").value = code;
    joinRoom();
}
