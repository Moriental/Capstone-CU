package com.example.capstonedesign.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OpeningHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayOfWeek; //월,화,수,목.금,토,일 혹은 매일이 들어감
    private String openTime; //시작일
    private String closeTime; // 종료일
    private String breakStartTime; // 브레이크 시작 시간
    private String breakEndTime; // 브레이크 끝나는 시간
    private String note; //휴무일인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;
    
}
