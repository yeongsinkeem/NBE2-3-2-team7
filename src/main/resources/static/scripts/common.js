const openMobileModal = document.getElementById('mobile-modal-open');
const closeMobileModal = document.getElementById('mobile-modal-close');
const mobileModal = document.getElementById('mobile-modal');
const backdrop = document.getElementById('mobile-modal-backdrop');
``

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
	// 현재 페이지가 로그인 페이지가 아닐 때만 인증 체크 실행
	if (window.location.pathname !== '/login') {
		checkAuthRequired();
	}

	// URL 파라미터에서 토큰 확인
	const token = new URLSearchParams(location.search).get('token');
	if (token) {
		TokenUtil.setToken(token);
		window.history.replaceState({}, document.title, window.location.pathname);
	}

	// UI 업데이트 실행
	updateAuthUI();

	// 모달 이벤트 리스너
	openMobileModal?.addEventListener('click', () => {
		mobileModal.classList.remove('hidden');
		backdrop.classList.remove('hidden');
	});

	closeMobileModal?.addEventListener('click', () => {
		mobileModal.classList.add('hidden');
		backdrop.classList.add('hidden');
	});

	backdrop?.addEventListener('click', () => {
		mobileModal.classList.add('hidden');
		backdrop.classList.add('hidden');
	});
});

function getCookie(name) {
	const value = `; ${document.cookie}`;
	const parts = value.split(`; ${name}=`);
	if (parts.length === 2) return parts.pop().split(';').shift();
}

function deleteCookie(name) {
	document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
}

// 인증이 필요한 페이지 체크
function checkAuthRequired() {
	const path = window.location.pathname;
	const authRequiredPaths = ['/mypage', '/payment'];

	if (authRequiredPaths.some(requiredPath => path.startsWith(requiredPath))) {
		if (!TokenUtil.hasToken()) {
			window.location.href = '/login';
			return false;
		}
	}
	return true;
}

// 인증 상태에 따른 UI 업데이트
function updateAuthUI() {
	const isAuthenticated = TokenUtil.hasToken();
	const loginButton = document.querySelector('#login-button');
	const logoutButton = document.querySelector('#logout-button');
	const myPageButton = document.querySelector('#mypage-button');
	const mobileLoginButton = document.querySelector('#mobile-login-button');
	const mobileLogoutButton = document.querySelector('#mobile-logout-button');
	const mobileMypageButton = document.querySelector('#mobile-mypage-button');

	if (isAuthenticated) {
		// PC 메뉴
		if (loginButton) loginButton.style.display = 'none';
		if (logoutButton) logoutButton.style.display = 'block';
		if (myPageButton) myPageButton.style.display = 'block';

		// 모바일 메뉴
		if (mobileLoginButton) mobileLoginButton.style.display = 'none';
		if (mobileLogoutButton) mobileLogoutButton.style.display = 'block';
		if (mobileMypageButton) mobileMypageButton.style.display = 'block';
	} else {
		// PC 메뉴
		if (loginButton) loginButton.style.display = 'block';
		if (logoutButton) logoutButton.style.display = 'none';
		if (myPageButton) myPageButton.style.display = 'none';

		// 모바일 메뉴
		if (mobileLoginButton) mobileLoginButton.style.display = 'block';
		if (mobileLogoutButton) mobileLogoutButton.style.display = 'none';
		if (mobileMypageButton) mobileMypageButton.style.display = 'none';
	}
}

async function handleMyPageClick(event) {
	event.preventDefault();
	const token = TokenUtil.getToken();

	if (!token) {
		window.location.href = '/login';
		return;
	}

	try {
		const response = await fetch('/mypage', {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${token}`,
				'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8',
			},
			redirect: 'follow'
		});

		if (response.ok) {
			window.location.href = '/mypage';
		} else {
			window.location.href = '/login';
		}
	} catch (error) {
		console.error('Error:', error);
		window.location.href = '/login';
	}
}
// 로그아웃 처리
async function handleLogout() {
	const token = TokenUtil.getToken();

	try {
		// 서버에 로그아웃 요청을 보냅니다
		const response = await fetch('/api/logout', {
			method: 'POST',
			headers: {
				...(token ? { 'Authorization': `Bearer ${token}` } : {}),
				'Content-Type': 'application/json'
			},
			credentials: 'include'  // 쿠키를 함께 전송
		});

		// 로컬의 토큰을 삭제
		TokenUtil.removeToken();

		// UI 업데이트
		updateAuthUI();

		// 메인 페이지로 리다이렉트
		window.location.href = '/';
	} catch (error) {
		console.error('Logout error:', error);
		// 에러가 발생하더라도 로컬의 토큰은 삭제하고 로그아웃 처리
		TokenUtil.removeToken();
		updateAuthUI();
		window.location.href = '/';
	}
}