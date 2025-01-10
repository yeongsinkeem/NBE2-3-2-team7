package com.project.popupmarket.dto.land;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
@ToString
public class RentalLandRequestTO {
    private RentalLandTO rentalLand;
    MultipartFile thumbnail;
    List<MultipartFile> images;
}
