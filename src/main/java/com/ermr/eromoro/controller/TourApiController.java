package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.service.SpotCommandService;
import com.ermr.eromoro.service.TourApiService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tour-api")
@RequiredArgsConstructor
public class TourApiController {

    private final TourApiService tourApiService;

    @Operation(summary = "[백엔드 개발자용] 관광지 지역(시도) 저장", description = " 관광지 지역(시도) 저장 API 입니다.")
    @PostMapping("/collect/sido-area")
    public ApiResponse<?> collectSidoArea(){
        tourApiService.collectSidoArea();
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "[백엔드 개발자용] 관광지 지역(시군구) 저장", description = " 관광지 지역(시군구) 저장 API 입니다.")
    @PostMapping("/collect/sigg-area")
    public ApiResponse<?> collectSiggArea(){
        tourApiService.collectSiggArea();
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "[백엔드 개발자용] 관광지 데이터 저장", description = " 관광지 데이터 저장 API 입니다.")
    @PostMapping("/collect/spot")
    public ApiResponse<?> collectSpot(){
        tourApiService.collectSpot();
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "[백엔드 개발자용] 관광지 상세정보 데이터 및 시설정보 데이터 저장", description = " 관광지 상세정보 데이터 및 시설정보 데이터 저장 API 입니다.")
    @PostMapping("/collect/info/facility")
    public ApiResponse<?> collectInfoAndFacility(){
        tourApiService.collectInfoAndFacility();
        return ApiResponse.onSuccess("");
    }
}
