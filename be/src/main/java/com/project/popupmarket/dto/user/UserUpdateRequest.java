package com.project.popupmarket.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserUpdateRequest {
    private String password;
    private String name;
    private String brand;
    private String tel;
    private MultipartFile profileImage;
}
