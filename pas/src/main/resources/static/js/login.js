const API_URL = "/api/auth";

// 로그인 함수
function login() {
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;

    if (!email || !password) {
        alert("이메일과 비밀번호를 입력하세요.");
        return;
    }

    fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert("로그인 성공!");
            localStorage.setItem("loggedInUser", email); // 로그인 정보 저장
            window.location.href = "index.html"; // 로그인 후 메인 페이지로 이동
        } else {
            alert("로그인 실패: " + data.message);
        }
    })
    .catch(error => console.error("로그인 오류:", error));
}

// 회원가입 함수
function register() {
    const email = document.getElementById("register-email").value;
    const password = document.getElementById("register-password").value;
    const confirmPassword = document.getElementById("register-confirm").value;

    if (!email || !password || !confirmPassword) {
        alert("모든 정보를 입력하세요.");
        return;
    }
    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    fetch(`${API_URL}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert("회원가입 성공!");
            toggleRegister();
        } else {
            alert("회원가입 실패: " + data.message);
        }
    })
    .catch(error => console.error("회원가입 오류:", error));
}

// 카카오 로그인
function kakaoLogin() {
    window.location.href = "/api/auth/kakao/login";
}

// 회원가입 / 로그인 화면 전환
function toggleRegister() {
    const loginContainer = document.querySelector(".login-container");
    const registerContainer = document.querySelector(".register-container");

    if (loginContainer.style.display === "none") {
        loginContainer.style.display = "block";
        registerContainer.style.display = "none";
    } else {
        loginContainer.style.display = "none";
        registerContainer.style.display = "block";
    }
}

// 페이지 로드 시 로그인 상태 확인 및 UI 업데이트
document.addEventListener("DOMContentLoaded", () => {
    const authButton = document.getElementById("authButton");
    const userGreeting = document.getElementById("userGreeting");

    const loggedInUser = localStorage.getItem("loggedInUser");

    if (loggedInUser) {
        authButton.textContent = "로그아웃";
        authButton.className = "logout-btn";
        userGreeting.textContent = `안녕하세요 ${loggedInUser} 님`;
        authButton.onclick = logout;
    } else {
        authButton.textContent = "로그인";
        authButton.className = "login-btn";
        userGreeting.textContent = ""; 
        authButton.onclick = () => window.location.href = "login.html";
    }
});

function logout() {
    localStorage.removeItem("loggedInUser");
    alert("로그아웃되었습니다.");
    location.reload();
}
