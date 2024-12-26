document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            // 비밀번호 확인
            const password = document.getElementById('password').value;
            const passwordCheck = document.getElementById('password-check').value;

            if (password !== passwordCheck) {
                alert('비밀번호가 일치하지 않습니다.');
                return;
            }

            // 서비스 약관 동의 확인
            const serviceCheck = document.getElementById('service-check');
            if (!serviceCheck.checked) {
                alert('서비스 약관에 동의해주세요.');
                return;
            }

            const formData = {
                email: document.getElementById('email').value,
                password: password,
                name: document.getElementById('name').value,
                brand: document.getElementById('brand').value,
                tel: document.getElementById('tel').value
            };

            try {
                const response = await fetch('/api/user', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(formData)
                });

                if (response.ok) {
                    alert('회원가입이 완료되었습니다.');
                    window.location.href = '/login';
                } else {
                    const errorData = await response.json();
                    alert(errorData.message || '회원가입에 실패했습니다. 입력 정보를 확인해주세요.');
                }
            } catch (error) {
                console.error('Registration error:', error);
                alert('회원가입 중 오류가 발생했습니다.');
            }
        });
    }

    // 폼 입력값 유효성 검사
    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    function validatePassword(password) {
        // 최소 8자, 최대 20자, 최소 하나의 문자와 하나의 숫자
        const re = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,20}$/;
        return re.test(password);
    }

    function validatePhone(phone) {
        const re = /^[0-9]{10,11}$/;
        return re.test(phone);
    }

    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const telInput = document.getElementById('tel');

    emailInput?.addEventListener('blur', () => {
        if (!validateEmail(emailInput.value)) {
            alert('올바른 이메일 형식을 입력해주세요.');
        }
    });

    passwordInput?.addEventListener('blur', () => {
        if (!validatePassword(passwordInput.value)) {
            alert('비밀번호는 8~20자의 영문자와 숫자 조합이어야 합니다.');
        }
    });

    telInput?.addEventListener('blur', () => {
        if (!validatePhone(telInput.value)) {
            alert('올바른 전화번호 형식을 입력해주세요.');
        }
    });
});