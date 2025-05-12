package com.example.capstonedesign.controller;

import com.example.capstonedesign.api.KaKaoPlaceCrawler;
import com.example.capstonedesign.domain.Place;
import com.example.capstonedesign.repository.PlaceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

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
    public String addPlace() throws IOException {
        Place place = kaKaoPlaceCrawler.crawlKakaoMap();
        placeRepository.save(place);
        return "redirect:/";
    }

}
