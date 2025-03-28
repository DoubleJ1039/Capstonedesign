let questions = [];
let currentIndex = -1;
let currentTemplate = 'classic'; // 기본 템플릿

function addQuestion() {
  const newQuestion = {
    type: "multiple",
    text: "",
    choices: ["", ""],
    image: ""
  };
  questions.push(newQuestion);
  currentIndex = questions.length - 1;
  renderQuestionList();
  loadQuestion(currentIndex);
}

function renderQuestionList() {
  const list = document.getElementById("question-list");
  list.innerHTML = "";
  questions.forEach((q, idx) => {
    const li = document.createElement("li");
    li.textContent = `문제 ${idx + 1}`;
    li.onclick = () => loadQuestion(idx);
    list.appendChild(li);
  });
}

function loadQuestion(index) {
  currentIndex = index;
  const q = questions[index];
  document.getElementById("question-type").value = q.type;
  document.getElementById("question-text").value = q.text;
  updateChoices();
  renderPreview();
}

function updateChoices() {
  const container = document.getElementById("choices-container");
  const type = document.getElementById("question-type").value;
  const q = questions[currentIndex];
  q.type = type;
  container.innerHTML = "";

  if (type === "ox") {
    q.choices = ["O", "X"];
  } else if (type === "short") {
    q.choices = [""];
    container.innerHTML = "<p>단답형은 보기 입력이 필요 없습니다.</p>";
  } else {
    if (!q.choices || q.choices.length < 2) q.choices = ["", ""];
    q.choices.forEach((choice, idx) => {
      const input = document.createElement("input");
      input.value = choice;
      input.placeholder = `보기 ${idx + 1}`;
      input.oninput = e => {
        q.choices[idx] = e.target.value;
        renderPreview();
      };
      container.appendChild(input);
    });

    if (q.choices.length < 4) {
      const addBtn = document.createElement("button");
      addBtn.textContent = "보기 추가";
      addBtn.onclick = () => {
        q.choices.push("");
        updateChoices();
      };
      container.appendChild(addBtn);
    }
  }

  renderPreview();
}

document.getElementById("question-text").addEventListener("input", e => {
  if (currentIndex >= 0) {
    questions[currentIndex].text = e.target.value;
    renderPreview();
  }
});

function handleImageUpload(event) {
  const file = event.target.files[0];
  if (!file || currentIndex < 0) return;

  const reader = new FileReader();
  reader.onload = function (e) {
    questions[currentIndex].image = e.target.result;
    renderPreview();
  };
  reader.readAsDataURL(file);
}

function renderPreview() {
    if (currentIndex < 0) return;
    const q = questions[currentIndex];
    document.getElementById("preview-text").textContent = q.text;
  
    const img = document.getElementById("preview-image");
    img.style.display = q.image ? "block" : "none";
    img.src = q.image || "";
  
    const container = document.getElementById("preview-choices");
    container.innerHTML = "";
  
    if (q.type === "multiple") {
      q.choices.forEach((choice, idx) => {
        const div = document.createElement("div");
        div.className = "preview-choice";
  
        const iconImg = document.createElement("img");
        iconImg.src = `images/choice${idx + 1}.png`;
  
        const label = document.createElement("div");
        label.textContent = choice;
  
        div.appendChild(iconImg);
        div.appendChild(label);
        container.appendChild(div);
      });
    } else if (q.type === "ox") {
      ["O", "X"].forEach((val, idx) => {
        const div = document.createElement("div");
        div.className = "preview-choice";
  
        const iconImg = document.createElement("img");
        iconImg.src = `images/choice${idx + 1}.png`;
  
        const label = document.createElement("div");
        label.textContent = val;
  
        div.appendChild(iconImg);
        div.appendChild(label);
        container.appendChild(div);
      });
    }
  }
  