package com.project.popupmarket.service;

import com.project.popupmarket.dto.PopupStoreTO;
import com.project.popupmarket.entity.PopupStore;
import com.project.popupmarket.repository.PopupStoreJpaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PopupStoreServiceImpl {
    @Autowired
    private PopupStoreJpaRepository popupStoreJpaRepository;

    // 데이터 삽입
    public int insert(PopupStoreTO to) {
        int flag = 0;

        // 받을 땐 TO로, 반환은 entity
        ModelMapper modelMapper = new ModelMapper();
        PopupStore popupStore = modelMapper.map(to, PopupStore.class);

        popupStoreJpaRepository.save(popupStore);
        return flag;
    }

    // 모든 팝업 리스트 출력
    public List<PopupStoreTO> findAll() {
        List<PopupStore> popupStores = popupStoreJpaRepository.findAll();

        ModelMapper modelMapper = new ModelMapper();
        List<PopupStoreTO> lists = popupStores.stream()
                .map( p -> modelMapper.map(p, PopupStoreTO.class) )
                .collect(Collectors.toList());

        return lists;
    }

    /*
    public PopupStoreTO findPopupStoreBySeq(Long id) {
        PopupStore popupStore = popupStoreJpaRepository.findPopupStoreBySeq(String.valueOf(id)).orElse(null);

        ModelMapper modelMapper = new ModelMapper();
        PopupStoreTO to = modelMapper.map(popupStore, PopupStoreTO.class);
        return to;
    }
     */
}
