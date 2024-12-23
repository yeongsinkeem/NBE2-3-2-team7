package com.project.popupmarket.service;

import com.project.popupmarket.dto.RentalPlaceImageListTO;
import com.project.popupmarket.dto.RentalPlaceTO;
import com.project.popupmarket.entity.RentalPlace;
import com.project.popupmarket.entity.RentalPlaceImageList;
import com.project.popupmarket.entity.RentalPlaceImageListId;
import com.project.popupmarket.repository.RentalPlaceImageListJpaRepository;
import com.project.popupmarket.repository.RentalPlaceJpaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentalPlaceServiceImpl {
    @Autowired
    private RentalPlaceJpaRepository rentalPlaceJpaRepository;
    @Autowired
    private RentalPlaceImageListJpaRepository rentalPlaceImageListJpaRepository;

    public List<RentalPlaceTO> findWithLimit() {
        List<Object[]> lists = rentalPlaceJpaRepository.findWithLimit();

        List<RentalPlaceTO> to = new ArrayList<>();

        for (Object[] result : lists) {
            RentalPlaceTO rentalPlaceTO = new RentalPlaceTO();
            rentalPlaceTO.setId((Long) result[0]); // seq
            rentalPlaceTO.setName((String) result[1]);                 // name
            rentalPlaceTO.setPrice((BigDecimal) result[2]);            // price
            rentalPlaceTO.setAddress((String) result[3]);              // address
            rentalPlaceTO.setThumbnail(result[4] != null
                    ? "/images/place_thumbnail/" + result[4]
                    : null);                                           // thumbnail
            to.add(rentalPlaceTO);
        }

        return to;
    }

    public RentalPlaceTO findById(Long id) {

        RentalPlace rentalPlace = rentalPlaceJpaRepository.findById(id).get();

        ModelMapper mapper = new ModelMapper();
        RentalPlaceTO to = mapper.map(rentalPlace, RentalPlaceTO.class);

        return to;
    }

    public RentalPlaceTO findDetailById(Long id) {
        Object result = rentalPlaceJpaRepository.findDetailById(id);

        Object[] objects = (Object[]) result;

        RentalPlaceTO rentalPlaceTO = new RentalPlaceTO();
        rentalPlaceTO.setZipcode((String) objects[0]);
        rentalPlaceTO.setPrice((BigDecimal) objects[1]);
        rentalPlaceTO.setAddress((String) objects[2]);
        rentalPlaceTO.setAddrDetail((String) objects[3]);
        rentalPlaceTO.setDescription((String) objects[4]);
        rentalPlaceTO.setInfra((String) objects[5]);
        rentalPlaceTO.setName((String) objects[6]);
        rentalPlaceTO.setCapacity((String) objects[7]);
        rentalPlaceTO.setNearbyAgeGroup((String) objects[8]);
        rentalPlaceTO.setRegisteredAt((Instant) objects[9]);

        return rentalPlaceTO;
    }



    public Page<RentalPlaceTO> findFilteredWithPagination(Integer minCapacity, Integer maxCapacity, String location, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Page<Object[]> lists = rentalPlaceJpaRepository.findFilteredWithPagination(minCapacity, maxCapacity, location, minPrice, maxPrice, pageable);
        System.out.println(lists);
        System.out.println("Content: " + lists.getContent());

//        rp.Capacity, rp.price, rp.name, rp.thumbnail, rp.registeredAt
        Page<RentalPlaceTO> to = lists.map(objects -> {
            RentalPlaceTO rentalPlaceTO = new RentalPlaceTO();
            rentalPlaceTO.setCapacity((String) objects[0]);
            rentalPlaceTO.setPrice((BigDecimal) objects[1]);
            rentalPlaceTO.setName((String) objects[2]);
            rentalPlaceTO.setThumbnail(objects[3] != null
                    ? "/images/place_thumbnail/" + objects[3]
                    : null);
            rentalPlaceTO.setRegisteredAt((Instant) objects[4]);
            return rentalPlaceTO;
        });

        return to;
    }

    public List<RentalPlaceImageListTO> findRentalPlaceImageList (Long id) {

        List<RentalPlaceImageList> lists = rentalPlaceImageListJpaRepository.findRentalPlaceImageList(id);

        List<RentalPlaceImageListTO> toList = new ArrayList<>();
        for (RentalPlaceImageList result : lists) {
            RentalPlaceImageListTO to = new RentalPlaceImageListTO();
            to.setRentalPlaceSeq(result.getId().getRentalPlaceSeq());
            to.setImage(result.getId().getImage());
            toList.add(to);
        }

        return toList;
    }

    public List<RentalPlaceTO> findRentalPlacesByUserId (Long id) {
        List<Object[]> results = rentalPlaceJpaRepository.findRentalPlacesByUserId(id);

        List<RentalPlaceTO> toList = new ArrayList<>();
        for (Object[] result : results) {
            RentalPlaceTO to = new RentalPlaceTO();
            to.setThumbnail((String) result[0]);
            to.setAddress((String) result[1]);
            to.setName((String) result[2]);
            to.setStatus((String) result[3]);
            toList.add(to);
        }

        return toList;
    }
    public int insertRentalPlace(RentalPlaceTO to){
        int flag=0;
        ModelMapper mapper = new ModelMapper();
        RentalPlace rentalPlace = mapper.map(to, RentalPlace.class);

        rentalPlaceJpaRepository.save(rentalPlace);

        return flag;
    }
    public int deleteRentalPlaceById(Long id){
        int flag=0;
        rentalPlaceJpaRepository.deleteById(id);

        return flag;
    }

}
