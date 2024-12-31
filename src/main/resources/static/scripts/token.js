const TOKEN_COOKIE_NAME = "jwt_token";

// 토큰 관련 유틸리티 함수들
const TokenUtil = {
    // 토큰 저장
    setToken(token) {
        localStorage.setItem("access_token", token);
    },

    // 토큰 가져오기
    getToken() {
        return localStorage.getItem("access_token") || getCookie(TOKEN_COOKIE_NAME);
    },

    // 토큰 존재 여부 확인
    hasToken() {
        return !!this.getToken();
    },

    // 토큰 제거
    removeToken() {
        localStorage.removeItem("access_token");
        deleteCookie(TOKEN_COOKIE_NAME);
    }
};

// 쿠키 관련 유틸리티 함수들
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

function deleteCookie(name) {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
}

// URL 파라미터에서 토큰 확인
const token = new URLSearchParams(location.search).get('token');
if (token) {
    TokenUtil.setToken(token);
    // 토큰 파라미터 제거
    window.history.replaceState({}, document.title, window.location.pathname);
}