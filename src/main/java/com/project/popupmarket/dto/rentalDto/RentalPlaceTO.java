package com.project.popupmarket.dto.rentalDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RentalPlaceTO {
    private Long id;
    private Long rentalUserSeqId;
    private String thumbnail;
    private String zipcode;
    private BigDecimal price;
    private String address;
    private String addrDetail;
    private String description;
    private String infra;
    private String name;
    private Integer area;
    private String nearbyAgeGroup;
    private Instant registeredAt;
    private String status;

    private Set<String> rentalPlaceImages;

    public RentalPlaceTO(Long id, Long rentalUserSeqId, String thumbnail, String zipcode,
                            BigDecimal price, String address, String addrDetail, String description,
                            String infra, String name, Integer area, String nearbyAgeGroup,
                            Instant registeredAt, String status) {
        this.id = id;
        this.rentalUserSeqId = rentalUserSeqId;
        this.thumbnail = thumbnail;
        this.zipcode = zipcode;
        this.price = price;
        this.address = address;
        this.addrDetail = addrDetail;
        this.description = description;
        this.infra = infra;
        this.name = name;
        this.area = area;
        this.nearbyAgeGroup = nearbyAgeGroup;
        this.registeredAt = registeredAt;
        this.status = status;
    }
}

