package com.project.popupmarket.dto.userDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserTO {
    private Long id;
    private String email;
    private String name;
    private String brand;
    private String profileImage;
    private String tel;
}
