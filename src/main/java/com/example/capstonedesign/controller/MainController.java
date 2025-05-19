package com.example.capstonedesign.controller;

import com.example.capstonedesign.service.KaKaoPlaceCrawler;
import com.example.capstonedesign.domain.Place;
import com.example.capstonedesign.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j

public class MainController {
    private final PlaceRepository placeRepository;
    private final KaKaoPlaceCrawler kaKaoPlaceCrawler;

    @Autowired
    public MainController(PlaceRepository placeRepository, KaKaoPlaceCrawler kaKaoPlaceCrawler) {
        this.placeRepository = placeRepository;
        this.kaKaoPlaceCrawler = kaKaoPlaceCrawler;
    }
    @GetMapping("/")
    public String index(Model model){
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
