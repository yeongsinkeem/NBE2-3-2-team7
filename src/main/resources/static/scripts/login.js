document.addEventListener('DOMContentLoaded', () => {
	const loginForm = document.getElementById('loginForm');

	if (loginForm) {
		loginForm.addEventListener('submit', async (e) => {
			e.preventDefault();

			const formData = {
				username: document.getElementById('email').value,
				password: document.getElementById('password').value
			};

			try {
				const response = await fetch('/api/token', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify(formData)
				});

				if (response.ok) {
					const data = await response.json();
					// JWT 토큰을 localStorage에 저장
					localStorage.setItem('token', data.token);
					// 메인 페이지로 리다이렉트
					window.location.href = '/main';
				} else {
					const errorData = await response.json();
					alert(errorData.message || '로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.');
				}
			} catch (error) {
				console.error('Login error:', error);
				alert('로그인 중 오류가 발생했습니다.');
			}
		});
	}

	// URL 파라미터에서 에러 메시지 확인
	const urlParams = new URLSearchParams(window.location.search);
	const error = urlParams.get('error');
	if (error) {
		alert('로그인에 실패했습니다. 다시 시도해주세요.');
	}
});