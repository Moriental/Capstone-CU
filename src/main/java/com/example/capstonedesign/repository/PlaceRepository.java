package com.example.capstonedesign.repository;

import com.example.capstonedesign.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place,Long> {
    List<Place> findByFoodType(String foodType);
}
