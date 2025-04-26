const API_URL = "/api";

const slidesData = [
  "images/banner1.png",
  "images/banner2.png",
  "images/banner3.png",
  "images/banner4.png",
  "images/banner5.png"
];

let currentIndex = 2;

document.addEventListener("DOMContentLoaded", () => {
  initAuth();
  loadRooms();
  initCarousel();
});

/* ----- 인증/로그인 관련 기존 코드 ----- */
function initAuth() {
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
}

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

  if (!roomCode || !roomPassword) {
    alert("방 코드와 비밀번호를 모두 입력하세요!");
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
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        alert("방에 참여하였습니다!");

        localStorage.setItem("roomPassword", roomPassword);
        localStorage.setItem("loggedInUser", loggedInUser);
    
        window.location.href = `quiz.html?code=${roomCode}`;
      } else {
        alert("방 참여 실패: " + data.message);
      }
    })    
    .catch(err => console.error("방 참여 오류:", err));
}

function loadRooms() {
  fetch(`${API_URL}/rooms/list`)
    .then(res => res.json())
    .then(data => {
      const roomsList = document.getElementById("rooms");
      roomsList.innerHTML = "";

      if (data.length === 0) {
        roomsList.innerHTML = "<li>방이 존재하지 않습니다.</li>";
        return;
      }

      data.slice(0, 4).forEach(room => {
        const li = document.createElement("li");
        li.textContent = `방 이름: ${room.name} / 코드: ${room.code}`;
        li.style.cursor = "pointer";
        li.onclick = () => joinRoomWithCode(room.code);
        roomsList.appendChild(li);
      });
    })
    .catch(err => console.error("방 목록 불러오기 오류:", err));
}


/* ================================
   캐러셀 관련
================================ */
function initCarousel() {
  const track = document.getElementById("carousel-track");
  for (let i = 0; i < 5; i++) {
    const slide = document.createElement("div");
    slide.className = "carousel-slide";
    const img = document.createElement("img");
    img.src = "";

    img.addEventListener("click", (e) => {
      const realIndex = parseInt(e.target.getAttribute("data-index"), 10);
      if (!isNaN(realIndex)) {
        currentIndex = realIndex;
        updateCarousel();
      }
    });

    slide.appendChild(img);
    track.appendChild(slide);
  }

  const prevBtn = document.querySelector(".carousel-button.prev");
  const nextBtn = document.querySelector(".carousel-button.next");

  prevBtn.addEventListener("click", () => {

    currentIndex = (currentIndex - 1 + slidesData.length) % slidesData.length;
    updateCarousel();
  });
  nextBtn.addEventListener("click", () => {
    currentIndex = (currentIndex + 1) % slidesData.length;
    updateCarousel();
  });
  updateCarousel();
}

function updateCarousel() {
  const slides = document.querySelectorAll(".carousel-slide");
  const rotatedArr = rotateArray(slidesData, currentIndex);

  slides.forEach((slide, i) => {
    const img = slide.querySelector("img");
    img.src = rotatedArr[i];
    const realIndex = slidesData.indexOf(rotatedArr[i]);
    img.setAttribute("data-index", realIndex);
    slide.classList.toggle("active", i === 2);
  });
}

function rotateArray(arr, centerIndex) {
  const len = arr.length; // 5

  const shift = 2 - centerIndex;

  return arr.map((_, i) => {
    return arr[(i - shift + len) % len];
  });
}
