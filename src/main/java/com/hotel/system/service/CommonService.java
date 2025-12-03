package com.hotel.system.service;

import com.hotel.system.repository.DictionaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final DictionaryRepository dictionaryRepository;

    /** Повертає меню ресторану для замовлення в номер */
    public List<Map<String, Object>> getRestaurantMenu() {
        return dictionaryRepository.getMenu();
    }

    /** Повертає список доступних послуг (СПА, трансфер тощо) */
    public List<Map<String, Object>> getHotelServices() {
        return dictionaryRepository.getServices();
    }
}