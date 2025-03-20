// 질문 목록을 가져오는 함수
function loadQuestions() {
    fetch('http://localhost:8080/api/questions')
        .then(response => response.json())
        .then(questions => {
            const questionsList = document.getElementById('questionsList');
            questionsList.innerHTML = '';
            
            questions.forEach(question => {
                const card = document.createElement('div');
                card.className = 'question-card';
                
                const createdAt = new Date(question.createdAt).toLocaleString('ko-KR');
                
                card.innerHTML = `
                    <h3>${question.title}</h3>
                    <p>${question.content}</p>
                    <div class="date">작성일: ${createdAt}</div>
                    <button onclick="deleteQuestion('${question.id}')">삭제</button>
                `;
                
                questionsList.appendChild(card);
            });
        })
        .catch(error => console.error('Error:', error));
}

// 새 질문을 제출하는 함수
function submitQuestion() {
    const title = document.getElementById('questionTitle').value;
    const content = document.getElementById('questionContent').value;
    
    if (!title || !content) {
        alert('제목과 내용을 모두 입력해주세요.');
        return;
    }
    
    fetch('http://localhost:8080/api/questions', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            title: title,
            content: content
        })
    })
    .then(response => response.json())
    .then(data => {
        document.getElementById('questionTitle').value = '';
        document.getElementById('questionContent').value = '';
        loadQuestions();
    })
    .catch(error => console.error('Error:', error));
}

// 질문을 삭제하는 함수
function deleteQuestion(id) {
    if (confirm('정말로 이 질문을 삭제하시겠습니까?')) {
        fetch(`http://localhost:8080/api/questions/${id}`, {
            method: 'DELETE'
        })
        .then(() => {
            loadQuestions();
        })
        .catch(error => console.error('Error:', error));
    }
}

// 페이지 로드 시 질문 목록 불러오기
document.addEventListener('DOMContentLoaded', loadQuestions); 