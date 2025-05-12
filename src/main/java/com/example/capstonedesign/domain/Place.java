package com.example.capstonedesign.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String address;

    private String phoneNumber;
    private String openingHours;
    private String image;
    private String starRate;

    @OneToMany(mappedBy = "place",cascade = CascadeType.ALL,orphanRemoval = true)
    public List<Menu> menus = new ArrayList<>();

    // 메뉴 추가 편의 메서드
    public void addMenu(Menu menu) {
        menus.add(menu);
        menu.setPlace(this);
    }

    public void removeMenu(Menu menu) {
        menus.remove(menu);
        menu.setPlace(null);
    }
}
