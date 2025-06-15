document.addEventListener("DOMContentLoaded", async () => {
  const params = new URLSearchParams(window.location.search);
  const roomCode = params.get("code");
  document.getElementById("roomCode").textContent = roomCode || "없음";

  if (!roomCode) {
    alert("방 코드가 없습니다.");
    return;
  }

  try {
    const res = await fetch(`/api/rooms/ranking?code=${roomCode}`);
    const data = await res.json();

    if (!data.success) {
      alert("랭킹 정보를 불러오는 데 실패했습니다.");
      return;
    }

    const tbody = document.getElementById("rankingBody");
    data.ranking.forEach((entry, index) => {
      const tr = document.createElement("tr");

      const rankTd = document.createElement("td");
      rankTd.textContent = index + 1;

      const nameTd = document.createElement("td");
      nameTd.textContent = entry.nickname || "익명";

      const scoreTd = document.createElement("td");
      scoreTd.textContent = entry.score || 0;

      tr.appendChild(rankTd);
      tr.appendChild(nameTd);
      tr.appendChild(scoreTd);

      tbody.appendChild(tr);
    });

  } catch (error) {
    console.error("랭킹 조회 오류:", error);
    alert("서버 오류로 랭킹을 불러올 수 없습니다.");
  }
});
