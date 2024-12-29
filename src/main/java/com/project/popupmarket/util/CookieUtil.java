//package com.project.popupmarket.util;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.util.SerializationUtils;
//
//import java.util.Base64;
//
//public class CookieUtil {
//
//    // 요청값을 바탕으로 쿠키 추가
//    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
//        Cookie cookie = new Cookie(name, value);
//        cookie.setPath("/");
//        cookie.setHttpOnly(true); // 보안 설정 추가
//        cookie.setMaxAge(maxAge);
//
//        response.addCookie(cookie);
//    }
//
//    // 쿠키 삭제
//    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
//        Cookie[] cookies = request.getCookies();
//
//        if (cookies == null) {
//            return;
//        }
//
//        for (Cookie cookie : cookies) {
//            if (name.equals(cookie.getName())) {
//                cookie.setValue("");
//                cookie.setPath("/");
//                cookie.setMaxAge(0);
//                cookie.setHttpOnly(true); // 보안 설정 추가
//                response.addCookie(cookie);
//            }
//        }
//    }
//
//    // 객체를 직렬화해 쿠키의 값으로 변환
//    public static String serialize(Object obj) {
//        return Base64.getUrlEncoder()
//                .encodeToString(SerializationUtils.serialize(obj));
//    }
//
//    // 쿠키를 역직렬화해 객체로 변환
//    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
//        return cls.cast(
//                SerializationUtils.deserialize(
//                        Base64.getUrlDecoder().decode(cookie.getValue())
//                )
//        );
//    }
//}