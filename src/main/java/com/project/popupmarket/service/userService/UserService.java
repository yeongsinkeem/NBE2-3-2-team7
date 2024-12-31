package com.project.popupmarket.service.userService;

import com.project.popupmarket.dto.userDto.UserRegisterDto;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Value("${app.default-profile-image}")
    private String defaultProfileImage;

    public Long save(UserRegisterDto dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .brand(dto.getBrand())
                .name(dto.getName())
                .tel(dto.getTel())
                .profileImage(defaultProfileImage)
                .build()).getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Long delete(Long userId) {
        userRepository.deleteById(userId);
        return userId;
    }
}