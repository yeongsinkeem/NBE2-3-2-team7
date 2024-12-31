// form submit 이벤트 리스너
document.addEventListener('DOMContentLoaded', function() {
	const loginForm = document.querySelector('form');
	if (loginForm) {
		loginForm.addEventListener('submit', function(e) {
			e.preventDefault();

			const formData = new FormData(this);
			const data = {
				email: formData.get('email'),
				password: formData.get('password')
			};

			fetch('/api/login', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(data)
			})
				.then(response => {
					if (response.ok) {
						// 응답 헤더나 body에서 리다이렉트 URL을 가져와서 이동
						const redirectUrl = response.headers.get('Location') || '/main';
						window.location.href = redirectUrl;
					} else {
						throw new Error('로그인 실패');
					}
				})
				.catch(error => {
					console.error('Login error:', error);
					// 에러 메시지 표시
					const errorDiv = document.querySelector('.error-message');
					if (errorDiv) {
						errorDiv.textContent = '이메일 또는 비밀번호가 올바르지 않습니다.';
					}
				});
		});
	}
});