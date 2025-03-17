const API_URL = "/api";

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

window.onload = loadRooms;
