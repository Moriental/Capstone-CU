package com.example.capstonedesign.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String menuName;
    private String price;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;
}
