const API_URL = "/api";

let currentImageTargetCode = null;

document.addEventListener("DOMContentLoaded", () => {
    const loggedInUser = localStorage.getItem("loggedInUser");
    const userGreeting = document.getElementById("userGreeting");

    if (!loggedInUser) {
        alert("로그인 후 이용 가능합니다.");
        window.location.href = "login.html";
        return;
    }

    if (userGreeting) {
        userGreeting.style.display = "inline-block";
        userGreeting.textContent = `안녕하세요 ${loggedInUser} 님`;
    }

    // 화면 로드 후 방 목록 불러오기
    loadRooms(loggedInUser);

    // 🔑 input 요소를 JS에서 가져와 이벤트 걸기
    const imageInput = document.getElementById("imageInput");
    imageInput.addEventListener("change", handleImageChange);
});

// 이미지 change 이벤트 핸들러
function handleImageChange(e) {
    console.log("✅ 파일 선택(change 이벤트) 발생");

    const file = e.target.files[0];
    if (!file) {
        console.log("⚠️ 파일이 선택되지 않음");
        return;
    }
    if (!currentImageTargetCode) {
        console.log("⚠️ currentImageTargetCode가 설정되지 않음");
        return;
    }

    console.log("🟡 선택된 파일:", file);

    const reader = new FileReader();
    reader.onloadend = function () {
        console.log("🟡 FileReader onloadend 발생");
        // data:image/png;base64,.... 형태이므로 앞부분 제거
        const base64Image = reader.result.split(',')[1];

        console.log("🔄 이미지 업로드 시작 (code):", currentImageTargetCode);

        // PUT /updateImage/{code}로 전송
        fetch(`${API_URL}/rooms/updateImage/${currentImageTargetCode}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ imageBase64: base64Image })
        })
        .then(res => res.json())
        .then(data => {
            console.log("✅ 서버 응답:", data);
            if (data.success) {
                alert("이미지가 업로드되었습니다!");
                loadRooms(localStorage.getItem("loggedInUser")); // 다시 방 목록 업데이트
            } else {
                alert("이미지 업로드 실패: " + data.message);
            }
        })
        .catch(err => {
            console.error("❌ 업로드 오류:", err);
        });
    };

    reader.readAsDataURL(file);
}

function handleLogout() {
    localStorage.removeItem("loggedInUser");
    alert("로그아웃되었습니다.");
    window.location.href = "login.html";
}

function createRoom() {
    const roomName = document.getElementById("room-name").value.trim();
    const roomPassword = document.getElementById("room-password").value.trim();
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
            window.location.href = `room-template.html?code=${data.roomCode}`;
        } else {
            alert("방 생성 실패: " + data.message);
        }
    })
    .catch(error => console.error("방 생성 오류:", error));
}

function loadRooms(loggedInUser) {
    console.log("🔄 방 목록 불러오기 시작");

    fetch(`${API_URL}/rooms/list`)
        .then(response => response.json())
        .then(data => {
            console.log("✅ 방 목록 불러오기 성공:", data);

            const roomGrid = document.getElementById("room-grid");
            roomGrid.innerHTML = "";

            const myRooms = data.filter(room => room.professorEmail === loggedInUser);

            if (myRooms.length === 0) {
                const noRoomText = document.createElement("div");
                noRoomText.textContent = "생성한 방이 없습니다.";
                noRoomText.style.marginTop = "20px";
                roomGrid.appendChild(noRoomText);
                return;
            }

            myRooms.forEach(room => {
                const card = document.createElement("div");
                card.className = "room-card";

                const img = document.createElement("img");
                img.src = room.imageBase64
                    ? `data:image/png;base64,${room.imageBase64}`
                    : "images/noimg.png";
                img.alt = "Room Image";
                img.className = "room-image";

                const title = document.createElement("div");
                title.textContent = room.name;

                const code = document.createElement("div");
                code.textContent = `코드: ${room.code}`;

                const deleteBtn = document.createElement("button");
                deleteBtn.textContent = "×";
                deleteBtn.className = "delete-btn";
                // 방 삭제
                deleteBtn.onclick = (event) => {
                    event.stopPropagation();
                    deleteRoom(room.code);
                };

                const imageBtn = document.createElement("button");
                imageBtn.textContent = "이미지 추가하기";
                // 이미지 추가
                imageBtn.onclick = (event) => {
                    event.stopPropagation();
                    console.log("🟢 이미지 추가 버튼 클릭됨");
                    currentImageTargetCode = room.code;
                    console.log("🟢 currentImageTargetCode:", currentImageTargetCode);
                    document.getElementById("imageInput").click();
                };

                card.appendChild(deleteBtn);
                card.appendChild(img);
                card.appendChild(title);
                card.appendChild(code);
                card.appendChild(imageBtn);

                card.onclick = () => {
                    window.location.href = `room-template.html?code=${room.code}`;
                };

                roomGrid.appendChild(card);
            });
        })
        .catch(error => console.error("방 목록 불러오기 오류:", error));
}

function deleteRoom(code) {
    if (!confirm("정말 이 방을 삭제하시겠습니까?")) return;

    fetch(`${API_URL}/rooms/delete/${code}`, { method: "DELETE" })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("방 삭제 성공!");
                loadRooms(localStorage.getItem("loggedInUser"));
            } else {
                alert("방 삭제 실패: " + data.message);
            }
        })
        .catch(error => console.error("방 삭제 오류:", error));
}
