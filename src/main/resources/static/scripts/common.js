// 기존 모달 관련 코드는 그대로 유지
const openMobileModal = document.getElementById('mobile-modal-open');
const closeMobileModal = document.getElementById('mobile-modal-close');
const mobileModal = document.getElementById('mobile-modal');
const backdrop = document.getElementById('mobile-modal-backdrop');

function updateAuthUI() {
	const isAuthenticated = TokenUtil.hasToken();
	const loginButton = document.querySelector('#login-button');
	const logoutButton = document.querySelector('#logout-button');
	const mobileLoginButton = document.querySelector('#mobile-login-button');
	const mobileLogoutButton = document.querySelector('#mobile-logout-button');
	const myPageButton = document.querySelector('#mypage-button');
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

// JWT 토큰 관련 유틸리티 추가
const TOKEN_COOKIE_NAME = "jwt_token";

const TokenUtil = {
	setToken(token) {
		localStorage.setItem("access_token", token);
	},

	getToken() {
		return localStorage.getItem("access_token") || getCookie(TOKEN_COOKIE_NAME);
	},

	hasToken() {
		return !!this.getToken();
	},

	removeToken() {
		localStorage.removeItem("access_token");
		deleteCookie(TOKEN_COOKIE_NAME);
	}
};

function getCookie(name) {
	const value = `; ${document.cookie}`;
	const parts = value.split(`; ${name}=`);
	if (parts.length === 2) return parts.pop().split(';').shift();
}

function deleteCookie(name) {
	document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
}

// 인증 상태에 따른 UI 업데이트
function updateAuthUI() {
	const isAuthenticated = TokenUtil.hasToken();
	const loginButton = document.querySelector('#login-button');
	const logoutButton = document.querySelector('#logout-button');
	const myPageButton = document.querySelector('#mypage-button');

	if (isAuthenticated) {
		if (loginButton) loginButton.style.display = 'none';
		if (logoutButton) logoutButton.style.display = 'block';
		if (myPageButton) myPageButton.style.display = 'block';
	} else {
		if (loginButton) loginButton.style.display = 'block';
		if (logoutButton) logoutButton.style.display = 'none';
		if (myPageButton) myPageButton.style.display = 'none';
	}
}

// 로그아웃 처리
function handleLogout() {
	fetch('/api/logout', {
		method: 'POST',
		headers: {
			'Authorization': `Bearer ${TokenUtil.getToken()}`
		}
	}).finally(() => {
		TokenUtil.removeToken();
		window.location.href = '/';
	});
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

// API 요청 시 토큰 추가
async function fetchWithToken(url, options = {}) {
	const token = TokenUtil.getToken();
	if (token) {
		options.headers = {
			...options.headers,
			'Authorization': `Bearer ${token}`
		};
	}
	return fetch(url, options);
}

// 페이지 로드 시 실행
document.addEventListener('DOMContentLoaded', function() {
	// 기존의 모달 이벤트 리스너들...
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

	// 인증 상태 체크 및 UI 업데이트
	checkAuthRequired();
	updateAuthUI();

	// URL 파라미터에서 토큰 확인
	const token = new URLSearchParams(location.search).get('token');
	if (token) {
		TokenUtil.setToken(token);
		// 토큰 파라미터 제거
		window.history.replaceState({}, document.title, window.location.pathname);
		updateAuthUI();
	}
});

// 기존의 다른 함수들은 그대로 유지
function getTomorrow() {
	// 기존 코드 유지
}

function createPriceSlider() {
	// 기존 코드 유지
}

function createPlaceSlider() {
	// 기존 코드 유지
}

function createPickCalendar() {
	// 기존 코드 유지
}

function createMap() {
	// 기존 코드 유지
}

function daumPostcode() {
	// 기존 코드 유지
}