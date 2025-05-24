package com.example.capstonedesign.dto;

import com.example.capstonedesign.domain.Place;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class placeDTO {
    private Long id;
    private String name;
    private String foodType;
    private String imageUrl;
    private String category;
    private double starRating;

    public static placeDTO from(Place place){
        return placeDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .foodType(place.getFoodType())
                .imageUrl(place.getImage())
                .category(place.getCategory())
                .starRating(place.getStarRate())
                .build();
    }
}
