package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.domain.SidoArea;
import com.ermr.eromoro.domain.SiggArea;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.TourApiResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public class TourApiConverter {

    public static List<SidoArea> toSidoAreaList (Mono<TourApiResponseDto.AreaCodeApiResponse> responseMono) {
        return responseMono
                .map(response -> response.getResponse().getBody().getItems().getItem().stream()
                        .map(item -> SidoArea.builder()
                                .code(item.getCode())
                                .name(item.getName())
                                .build())
                        .toList())
                .block();
    }

    public static List<SiggArea> toSiggAreaList(SidoArea sidoArea, Mono<TourApiResponseDto.AreaCodeApiResponse> responseMono) {
        return responseMono
                .map(response -> response.getResponse().getBody().getItems().getItem().stream()
                        .map(item -> SiggArea.builder()
                                .sidoArea(sidoArea)
                                .code(item.getCode())
                                .name(item.getName())
                                .build())
                        .toList())
                .block();
    }
}
