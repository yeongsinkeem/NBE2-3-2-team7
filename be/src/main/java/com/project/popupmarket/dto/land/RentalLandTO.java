package com.project.popupmarket.dto.land;

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
public class RentalLandTO {
    private Long id;
    private Long landlordId;
    private String thumbnail;
    private BigDecimal price;
    private String zipcode;
    private String address;
    private String addrDetail;
    private String description;
    private String infra;
    private String name;
    private Integer area;
    private String ageGroup;
    private Instant registeredAt;
    private String status;

    private Set<String> rentalPlaceImages;

    public RentalLandTO(Long id, Long landlordId, String thumbnail, String zipcode,
                        BigDecimal price, String address, String addrDetail, String description,
                        String infra, String name, Integer area, String ageGroup,
                        Instant registeredAt, String status) {
        this.id = id;
        this.landlordId = landlordId;
        this.thumbnail = thumbnail;
        this.zipcode = zipcode;
        this.price = price;
        this.address = address;
        this.addrDetail = addrDetail;
        this.description = description;
        this.infra = infra;
        this.name = name;
        this.area = area;
        this.ageGroup = ageGroup;
        this.registeredAt = registeredAt;
        this.status = status;
    }
}

