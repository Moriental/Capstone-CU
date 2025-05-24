package com.example.capstonedesign.controller;

import com.example.capstonedesign.dto.placeDTO;
import com.example.capstonedesign.service.KaKaoPlaceCrawler;
import com.example.capstonedesign.domain.Place;
import com.example.capstonedesign.repository.PlaceRepository;
import com.example.capstonedesign.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j

public class MainController {
    private final PlaceRepository placeRepository;
    private final KaKaoPlaceCrawler kaKaoPlaceCrawler;
    private final MainService mainService;

    @Autowired
    public MainController(PlaceRepository placeRepository, KaKaoPlaceCrawler kaKaoPlaceCrawler, MainService mainService) {
        this.placeRepository = placeRepository;
        this.kaKaoPlaceCrawler = kaKaoPlaceCrawler;
        this.mainService = mainService;
    }
    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String foodType) {
        List<placeDTO> placesDto; // DTO 리스트 사용
        if (foodType != null && !foodType.isEmpty() && !foodType.equals("모두")) {
            placesDto = mainService.getPlacesByFoodTypeDto(foodType);
        } else {
            placesDto = mainService.getAllPlacesDto();
        }
        model.addAttribute("places", placesDto); // 모델에 DTO 리스트를 추가
        model.addAttribute("selectedFoodType", foodType != null ? foodType : "모두");
        return "index";
    }

    @GetMapping("/index/{id}")
    public String place(@PathVariable Long id, Model model){
        model.addAttribute("place", placeRepository.findById(id));
        return "index";
    }

    @GetMapping("/places")
    public String addAllPlaces() throws IOException {
        List<Place> Places = new ArrayList<>();
        Places.addAll(kaKaoPlaceCrawler.crawlKoreanPlaces());
        Places.addAll(kaKaoPlaceCrawler.crawlJapanesePlaces());
        Places.addAll(kaKaoPlaceCrawler.crawlChinesePlaces());
        Places.addAll(kaKaoPlaceCrawler.crawlWesternPlaces());

        for (Place place : Places) {
            placeRepository.save(place);
        }
        return "redirect:/";
    }
}
