//package com.project.popupmarket.service.userService;
//
//import com.project.popupmarket.entity.User;
//import com.project.popupmarket.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class OAuth2UserCustomService extends DefaultOAuth2UserService {
//
//    private final UserRepository userRepository;
//
//    @Value("${app.default-profile-image}")
//    private String defaultProfileImage;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User user = super.loadUser(userRequest); // ❶ 요청을 바탕으로 유저 정보를 담은 객체 반환
//        saveOrUpdate(user);
//
//        return user;
//    }
//
//    // 유저가 있으면 업데이트, 없으면 유저 생성
//    private User saveOrUpdate(OAuth2User oAuth2User) {
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//
//        String email = getKakaoEmail(attributes);
//        String nickname = getKakaoNickname(attributes);
//
//        User user = userRepository.findByEmail(email)
//                .map(entity -> {
//                    entity.setName(nickname);
//                    return entity;
//                })
//                .orElse(User.builder()
//                        .email(email)
//                        .name(nickname)
//                        .profile_image(defaultProfileImage)
//                        .build());
//
//        return userRepository.save(user);
//    }
//
//    private String getKakaoEmail(Map<String, Object> attributes) {
//        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//        return (String) kakaoAccount.get("email");
//    }
//
//    private String getKakaoNickname(Map<String, Object> attributes) {
//        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
//        return (String) properties.get("nickname");
//    }
//}