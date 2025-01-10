package com.project.popupmarket.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserRegisterDto {
    @NotEmpty(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotEmpty(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    @NotEmpty(message = "이름은 필수입니다")
    private String name;

    @NotEmpty(message = "브랜드명은 필수입니다")
    private String brand;

    @NotEmpty(message = "전화번호는 필수입니다")
    private String tel;
}
