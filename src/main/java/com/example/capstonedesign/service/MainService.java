package com.example.capstonedesign.service;


import com.example.capstonedesign.domain.Place;
import com.example.capstonedesign.dto.placeDTO;
import com.example.capstonedesign.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainService {
    private final PlaceRepository placeRepository;

    @Autowired
    public MainService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public List<placeDTO> getAllPlacesDto() {
        List<Place> places = placeRepository.findAll();
        List<placeDTO> placeDtos = new ArrayList<>();
        for (Place place : places) {
            placeDtos.add(placeDTO.from(place));
        }
        return placeDtos;
    }
    public List<placeDTO> getPlacesByFoodTypeDto(String foodType) {
        List<Place> places = placeRepository.findByFoodType(foodType);
        List<placeDTO> placeDtos = new ArrayList<>();
        for (Place place : places) {
            placeDtos.add(placeDTO.from(place));
        }
        return placeDtos;
    }
}
