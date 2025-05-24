package com.example.capstonedesign.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OpeningHoursDTO {
    private String dayOfWeek;
    private String openTime;
    private String closeTime;
    private String breakStartTime;
    private String breakEndTime;
    private String note;

    public static OpeningHoursDTO from(OpeningHoursDTO openingHoursDTO){
        return OpeningHoursDTO.builder()
                .dayOfWeek(openingHoursDTO.getDayOfWeek())
                .openTime(openingHoursDTO.getOpenTime())
                .closeTime(openingHoursDTO.getCloseTime())
                .breakStartTime(openingHoursDTO.getBreakStartTime())
                .breakEndTime(openingHoursDTO.getBreakEndTime())
                .note(openingHoursDTO.getNote())
                .build();
    }
}
