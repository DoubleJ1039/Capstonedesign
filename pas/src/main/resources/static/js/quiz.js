document.addEventListener('DOMContentLoaded', () => {
    const createQuizButton = document.getElementById('create-quiz-button');
    const createQuizContainer = document.getElementById('create-quiz-container');
    const quizContainer = document.getElementById('quiz-container');
    const addAnswerButton = document.getElementById('add-answer');
    const submitQuizButton = document.getElementById('submit-quiz-button');
    const extraAnswersContainer = document.getElementById('extra-answers');

    let questions = JSON.parse(localStorage.getItem('quizData')) || [];

    // ✅ "퀴즈 만들기" 버튼 클릭 시 폼 표시
    createQuizButton.addEventListener('click', () => {
        createQuizContainer.classList.toggle('hide');
        quizContainer.classList.add('hide');
    });

    // ✅ "추가 정답/오답" 버튼 클릭 시 새 입력 필드 추가
    addAnswerButton.addEventListener('click', () => {
        const input = document.createElement('input');
        input.type = 'text';
        input.placeholder = '추가 정답/오답 입력';
        extraAnswersContainer.appendChild(input);
    });

    // ✅ "퀴즈 제출" 버튼 클릭 시 퀴즈 저장
    submitQuizButton.addEventListener('click', () => {
        const questionText = document.getElementById('quiz-question').value;
        const answer1 = document.getElementById('quiz-answer1').value;
        const answer2 = document.getElementById('quiz-answer2').value;
        const extraInputs = document.querySelectorAll('#extra-answers input');

        if (!questionText || !answer1 || !answer2) {
            alert("질문과 최소 두 개의 답변을 입력하세요!");
            return;
        }

        let answers = [
            { text: answer1, correct: true },
            { text: answer2, correct: false }
        ];

        extraInputs.forEach(input => {
            if (input.value.trim() !== "") {
                answers.push({ text: input.value, correct: false });
            }
        });

        questions.push({ question: questionText, answers });
        localStorage.setItem('quizData', JSON.stringify(questions));

        alert("퀴즈가 추가되었습니다!");
        createQuizContainer.classList.add('hide');
    });
});
