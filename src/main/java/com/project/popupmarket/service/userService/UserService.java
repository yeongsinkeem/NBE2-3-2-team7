package com.project.popupmarket.service.userService;

import com.project.popupmarket.dto.userDto.UserRegisterDto;
import com.project.popupmarket.dto.userDto.UserUpdateRequest;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Value("${app.upload-path}")
    private String uploadPath;

    @Value("${app.default-profile-image}")
    private String defaultProfileImage = "default_profile.png";

    private final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
    private final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

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

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 비밀번호 업데이트
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(encoder.encode((request.getPassword())));
        }

        // 기본 정보 업데이트
        if (StringUtils.hasText(request.getName())) user.setName(request.getName());
        if (StringUtils.hasText(request.getBrand())) user.setBrand(request.getBrand());
        if (StringUtils.hasText(request.getTel())) user.setTel(request.getTel());

        // 프로필 이미지 처리
        MultipartFile profileImage = request.getProfileImage();
        if (profileImage != null && !profileImage.isEmpty()) {
            validateProfileImage(profileImage);
            String oldImage = user.getProfileImage();
            String newFileName = saveProfileImage(profileImage);
            user.setProfileImage(newFileName);

            // 기존 이미지 삭제 (기본 이미지가 아닌 경우에만)
            if (oldImage != null && !oldImage.equals(defaultProfileImage)) {
                deleteProfileImage(oldImage);
            }
        }

        userRepository.save(user);
    }

    private void validateProfileImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new RuntimeException("Invalid file name");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new RuntimeException("Invalid file type. Only " + String.join(", ", ALLOWED_EXTENSIONS) + " are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum limit of 2MB");
        }
    }

    private String saveProfileImage(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            String newFileName = UUID.randomUUID() + fileExtension;

            Path uploadDirectory = Paths.get(uploadPath);
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }

            Path filePath = uploadDirectory.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store profile image", e);
        }
    }

    private void deleteProfileImage(String fileName) {
        try {
            Path filePath = Paths.get(uploadPath).resolve(fileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete profile image", e);
        }
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 프로필 이미지 삭제 (기본 이미지가 아닌 경우에만)
        String profileImage = user.getProfileImage();
        if (profileImage != null && !profileImage.equals(defaultProfileImage)) {
            deleteProfileImage(profileImage);
        }

        // 사용자 삭제
        userRepository.delete(user);
    }
}