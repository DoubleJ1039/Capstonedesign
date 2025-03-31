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

    loadRooms(loggedInUser);

    const imageInput = document.getElementById("imageInput");
    imageInput.addEventListener("change", handleImageChange);
});

function handleImageChange(e) {
    const file = e.target.files[0];
    if (!file) return;
    if (!currentImageTargetCode) return;

    const reader = new FileReader();
    reader.onloadend = function () {
        const base64Image = reader.result.split(',')[1];

        fetch(`${API_URL}/rooms/updateImage/${currentImageTargetCode}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ imageBase64: base64Image })
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert("이미지가 업로드되었습니다!");
                loadRooms(localStorage.getItem("loggedInUser"));
            } else {
                alert("이미지 업로드 실패: " + data.message);
            }
        })
        .catch(err => {
            console.error("업로드 오류:", err);
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
    fetch(`${API_URL}/rooms/list`)
        .then(response => response.json())
        .then(data => {
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

                // 이미지 + 휴지통 버튼 wrapper
                const imgWrapper = document.createElement("div");
                imgWrapper.className = "room-image-wrapper";

                const img = document.createElement("img");
                img.src = room.imageBase64
                    ? `data:image/png;base64,${room.imageBase64}`
                    : "images/noimg.png";
                img.alt = "Room Image";
                img.className = "room-image";

                const deleteBtn = document.createElement("button");
                deleteBtn.className = "delete-btn";
                const trashImg = document.createElement("img");
                trashImg.src = "images/trash.png";
                trashImg.alt = "삭제";
                deleteBtn.appendChild(trashImg);
                deleteBtn.onclick = (event) => {
                    event.stopPropagation();
                    deleteRoom(room.code);
                };

                imgWrapper.appendChild(img);
                imgWrapper.appendChild(deleteBtn);

                // 텍스트 정보
                const info = document.createElement("div");
                info.className = "room-info";
                const title = document.createElement("div");
                title.textContent = room.name;
                const code = document.createElement("div");
                code.textContent = `코드: ${room.code}`;
                info.appendChild(title);
                info.appendChild(code);

                // 버튼 그룹
                const btnGroup = document.createElement("div");
                btnGroup.className = "room-buttons";

                const imageBtn = document.createElement("button");
                imageBtn.textContent = "이미지 변경";
                imageBtn.onclick = (event) => {
                    event.stopPropagation();
                    currentImageTargetCode = room.code;
                    document.getElementById("imageInput").click();
                };

                const qrBtn = document.createElement("button");
                qrBtn.textContent = "QR";
                qrBtn.onclick = (event) => {
                    event.stopPropagation();
                    showQRCode(room.code);
                };

                btnGroup.appendChild(imageBtn);
                btnGroup.appendChild(qrBtn);

                card.appendChild(imgWrapper);
                card.appendChild(info);
                card.appendChild(btnGroup);

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

// QR 코드 표시 함수
function showQRCode(roomCode) {
    const modal = document.getElementById("qrModal");
    const qrCodeDiv = document.getElementById("qrCode");
    const roomCodeText = document.getElementById("roomCodeText");

    const qr = qrcode(0, 'M');
    qr.addData(`${window.location.origin}/index.html?code=${roomCode}`);
    qr.make();

    qrCodeDiv.innerHTML = qr.createImgTag(8);
    roomCodeText.textContent = `방 코드: ${roomCode}`;

    modal.style.display = "block";

    const closeBtn = modal.querySelector(".close");
    closeBtn.onclick = function () {
        modal.style.display = "none";
    };

    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };
}
