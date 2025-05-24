package com.example.capstonedesign.dto;

import com.example.capstonedesign.domain.Menu;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MenuDTO {
    private String name;
    private String price;

    public static MenuDTO from(Menu menu) {
        return MenuDTO.builder()
                .name(menu.getMenuName())
                .price(menu.getPrice())
                .build();
    }
}
