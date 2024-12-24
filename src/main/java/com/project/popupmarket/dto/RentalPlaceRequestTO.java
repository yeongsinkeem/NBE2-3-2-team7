package com.project.popupmarket.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@Setter
@ToString
public class RentalPlaceRequestTO {
    private RentalPlaceTO rentalPlace;
    MultipartFile thumbnail;
    List<MultipartFile> images;
}
