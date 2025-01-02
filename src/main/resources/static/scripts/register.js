document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const formData = {
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            name: document.getElementById('name').value,
            brand: document.getElementById('brand').value,
            tel: document.getElementById('tel').value
        };

        try {
            const response = await fetch('/api/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            if (response.ok) {
                const data = await response.json(); // JSON 응답을 파싱

                // 토큰 저장
                if (data.token) {
                    TokenUtil.setToken(data.token);
                }

                // 메인 페이지로 이동
                window.location.href = data.redirectUrl || '/main';
            } else {
                const error = await response.text();
                alert('회원가입 실패: ' + error);
            }
        } catch (error) {
            console.error('Error:', error);
            alert('서버 오류가 발생했습니다.');
        }
    });
});