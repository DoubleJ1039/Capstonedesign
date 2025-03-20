const API_URL = "/api";

document.addEventListener("DOMContentLoaded", () => {
    const loggedInUser = localStorage.getItem("loggedInUser");
    const userGreeting = document.getElementById("userGreeting");

    if (!loggedInUser) {
        alert("로그인 후 이용 가능합니다.");
        window.location.href = "login.html";
        return;
    }

    userGreeting.textContent = `안녕하세요 ${loggedInUser} 님`;
    loadRooms();
});

function handleLogout() {
    localStorage.removeItem("loggedInUser");
    alert("로그아웃되었습니다.");
    window.location.href = "login.html";
}

//방 생성
function createRoom() {
    const roomName = document.getElementById("room-name").value;
    const roomPassword = document.getElementById("room-password").value;
    const professorEmail = localStorage.getItem("loggedInUser");

    if (!roomName || !roomPassword) {
        alert("방 이름과 비밀번호를 입력하세요!");
        return;
    }

    fetch(`${API_URL}/rooms/create`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: roomName, password: roomPassword, professorEmail })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert(`방 생성 성공! 코드: ${data.roomCode}`);
            loadRooms();
        } else {
            alert("방 생성 실패: " + data.message);
        }
    })
    .catch(error => console.error("방 생성 오류:", error));
}

//방 목록 불러오기
function loadRooms() {
    fetch(`${API_URL}/rooms/list`)
        .then(response => response.json())
        .then(data => {
            const roomsList = document.getElementById("rooms-list");
            const roomSelect = document.getElementById("room-select");
            roomsList.innerHTML = "";
            roomSelect.innerHTML = '<option value="">방을 선택하세요</option>';

            data.forEach(room => {
                const li = document.createElement("li");
                li.innerHTML = `${room.name} (코드: ${room.code}) 
                                <button onclick="deleteRoom('${room.code}')">삭제</button>`;
                roomsList.appendChild(li);

                const option = document.createElement("option");
                option.value = room.code;
                option.textContent = room.name;
                roomSelect.appendChild(option);
            });
        })
        .catch(error => console.error("방 목록 불러오기 오류:", error));
}

//방 삭제
function deleteRoom(code) {
    if (!confirm("정말 이 방을 삭제하시겠습니까?")) return;

    fetch(`${API_URL}/rooms/delete/${code}`, { method: "DELETE" })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("방 삭제 성공!");
                loadRooms();
            } else {
                alert("방 삭제 실패: " + data.message);
            }
        })
        .catch(error => console.error("방 삭제 오류:", error));
}

window.onload = loadRooms;
