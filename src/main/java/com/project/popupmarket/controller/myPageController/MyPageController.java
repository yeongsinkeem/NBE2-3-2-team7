package com.project.popupmarket.controller.myPageController;

import com.project.popupmarket.dto.userDto.UserUpdateRequest;
import com.project.popupmarket.entity.User;
import com.project.popupmarket.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Controller
public class MyPageController {

    private final UserService userService;

    // 공통으로 사용할 사용자 정보 조회 메서드
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        return userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/api/user")
    @ResponseBody
    public ResponseEntity<User> getUserInfo() {
        try {
            return ResponseEntity.ok(getCurrentUser());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/mypage")
    public String mypage(Model model) {
        try {
            model.addAttribute("user", getCurrentUser());
            return "mypage";
        } catch (Exception e) {
            return "redirect:/login";
        }
    }

    @PutMapping("/api/updateUser")
    public ResponseEntity<?> updateUser(
            @RequestPart(value = "userUpdateRequest") UserUpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        try {
            User currentUser = getCurrentUser(); // 앞서 만든 getCurrentUser() 메서드 활용
            request.setProfileImage(profileImage);
            userService.updateUser(currentUser.getId(), request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/api/deleteUser")
    public ResponseEntity<?> deleteUser() {
        try {
            User currentUser = getCurrentUser();
            userService.deleteUser(currentUser.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
