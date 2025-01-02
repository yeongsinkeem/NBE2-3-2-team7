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
});

// 메뉴 열기
openMobileModal.addEventListener('click', () => {
	mobileModal.classList.remove('hidden'); // 메뉴를 보이게 함
	backdrop.classList.remove('hidden'); // 배경 어두운 부분을 보이게 함
});

// 메뉴 닫기
closeMobileModal.addEventListener('click', () => {
	mobileModal.classList.add('hidden'); // 메뉴를 숨김
	backdrop.classList.add('hidden'); // 배경을 숨김
});

// 배경 클릭 시 메뉴 닫기
backdrop.addEventListener('click', () => {
	mobileModal.classList.add('hidden');
	backdrop.classList.add('hidden');
});

// 기존의 다른 함수들은 그대로 유지
function getTomorrow() {
	const today = new Date();

	const tomorrow = new Date(today);
	tomorrow.setDate(today.getDate() + 1);

	const year = tomorrow.getFullYear();
	const month = tomorrow.getMonth() + 1;
	const day = tomorrow.getDate();

	return `${year}-${month < 10 ? '0' + month : month}-${day < 10 ? '0' + day : day}`;
}

function createPriceSlider() {
	let slider = document.getElementById('price-slider');

	noUiSlider.create(slider, {
		start: [10, 1000],
		connect: true,
		range: {
			'min': 10,
			'max': 1000
		},
		format: {
			to: function (value) {
				if(value >= 1000) return '제한 없음';
				return Math.round(value) + '만원';
			},
			from: function (value) {
				return Number(value.replace('만원', ''));
			}
		},
	});

	let priceRange = document.getElementById('price-range');

	slider.noUiSlider.on('update', function(values, handle) {
		priceRange.innerHTML = `${values[0]} ~ ${values[1]}`;
	})
}

function createPlaceSlider() {
	let slider = document.getElementById('place-slider');

	noUiSlider.create(slider, {
		start: [0, 100],
		connect: true,
		range: {
			'min': 0,
			'max': 100
		},
		format: {
			to: function (value) {
				if(value >= 100) return '제한 없음';
				return Math.round(value) + '평';
			},
			from: function (value) {
				return Number(value.replace('평', ''));
			}
		},
	});

	let priceRange = document.getElementById('place-range');

	slider.noUiSlider.on('update', function(values, handle) {
		priceRange.innerHTML = `${values[0]} ~ ${values[1]}`;
	})
}

let calendarInstance = null;

function createPickCalendar() {

	if (calendarInstance) {
		calendarInstance.destroy();
	}

	calendarInstance = flatpickr('#quick-calendar', {
		mode: "range",
		dateFormat: "Y-m-d",
		minDate: getTomorrow(),
		locale: {
			rangeSeparator: " ~ ",
			weekdays: {
				shorthand: ["일", "월", "화", "수", "목", "금", "토"],  // 요일 이름 (짧은 형태)
				longhand: ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"],  // 요일 이름 (긴 형태)
			},
			months: {
				shorthand: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],  // 월 이름 (짧은 형태)
				longhand: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],  // 월 이름 (긴 형태)
			},
		}
	});
}

function createMap() {
	let container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
	let options = { //지도를 생성할 때 필요한 기본 옵션
		center: new kakao.maps.LatLng(33.450701, 126.570667), //지도의 중심좌표.
		level: 3 //지도의 레벨(확대, 축소 정도)
	};

	let map = new kakao.maps.Map(container, options); //지도 생성 및 객체 리턴
}

//본 예제에서는 도로명 주소 표기 방식에 대한 법령에 따라, 내려오는 데이터를 조합하여 올바른 주소를 구성하는 방법을 설명합니다.
function daumPostcode() {
	const postcode = document.getElementById('postcode');
	const addr = document.getElementById('addr');

	new daum.Postcode({
		oncomplete: function(data) {
			var roadAddr = data.roadAddress; // 도로명 주소 변수

			postcode.value = data.zonecode;
			addr.value = roadAddr;
		}
	}).open();
}

function getPeriod(startDate, endDate){
	const start = new Date(startDate);
	const end = new Date(endDate);

	return (end - start) / (1000 * 3600 * 24) + 1;
}