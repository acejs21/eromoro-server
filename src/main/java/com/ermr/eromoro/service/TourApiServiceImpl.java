package com.ermr.eromoro.service;

import com.ermr.eromoro.converter.TourApiConverter;
import com.ermr.eromoro.domain.*;
import com.ermr.eromoro.domain.enums.FacilityInfo;
import com.ermr.eromoro.dto.TourApiResponseDto;
import com.ermr.eromoro.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourApiServiceImpl implements TourApiService {

    @Value("${tour-api.service-key}")
    private String serviceKey;
    @Value("${tour-api.spot-info-base-url}")
    private String spotInfobaseUrl;
    @Value("${tour-api.barrier-free-trip-base-url}")
    private String barrierFreeTripBaseUrl;
    private final WebClient.Builder webClientBuilder;
    private final SidoAreaRepository sidoAreaRepository;
    private final SiggAreaRepository siggAreaRepository;
    private final SpotRepository spotRepository;
    private final FacilityInformationRepository facilityInformationRepository;
    private final SpotFacilityInformationRepository spotFacilityInformationRepository;

    @Override
    public void collectSidoArea() {
        sidoAreaRepository.deleteAll();
        List<SidoArea> sidoAreaList = TourApiConverter.toSidoAreaList(getAreaCode(null));
        sidoAreaRepository.saveAll(sidoAreaList);
    }

    @Override
    public void collectSiggArea() {
        siggAreaRepository.deleteAll();
        sidoAreaRepository.findAll().forEach(sidoArea -> {
            // 외부 API 호출 결과를 서비스 경계에서만 block
            List<TourApiResponseDto.AreaCodeApiResponse.Item> items =
                    getAreaCode(Integer.valueOf(sidoArea.getCode()))
                            .map(res -> res.getResponse().getBody().getItems().getItem())
                            .blockOptional()
                            .orElseGet(Collections::emptyList);
            List<SiggArea> siggAreaList = new ArrayList<>();
            for(var item : items) {
                SiggArea newSiggArea = TourApiConverter.toSiggArea(item);
                newSiggArea.setSidoArea(sidoArea);
                siggAreaList.add(newSiggArea);
            }
            siggAreaRepository.saveAll(siggAreaList);
        });
    }

    @Override
    public void collectSpot() {
        spotRepository.deleteAll();
        siggAreaRepository.findAll().forEach(siggArea -> {
            List<Spot> spotList = TourApiConverter.toSpot(
                    siggArea,
                    getAreaBasedSpotList(
                            Integer.valueOf(siggArea.getSidoArea().getCode()),
                            Integer.valueOf(siggArea.getCode())
                    )
            );
            spotRepository.saveAll(spotList);
        });
//        SiggArea siggArea = siggAreaRepository.findById(1405L).get();
//        System.out.println(siggArea.getSidoArea().getName() + " - " + siggArea.getName());
//        List<Spot> spotList = TourApiConverter.toSpot(
//                siggArea,
//                getAreaBasedSpotList(
//                        Integer.valueOf(siggArea.getSidoArea().getCode()),
//                        Integer.valueOf(siggArea.getCode())
//                )
//        );
//        for (Spot spot : spotList) {
//            System.out.println(spot.getTitle() + " - " + spot.getContentId());
//        }
//        spotRepository.saveAll(spotList);
    }

    @Override
    @Transactional
    public void collectInfoAndFacility() {
        spotRepository.findTop800By().forEach(spot -> {
            // 관광지 상세 정보 업데이트
            TourApiResponseDto.TourOverviewApiResponse responseDto =
                    getDetailCommon(spot.getContentId())
                            .blockOptional()
                            .orElse(null);
            if (responseDto != null) {
                spot.updateInfo(responseDto.getResponse().getBody().getItems().getItem().get(0).getOverview());
            }
            // 관광지 시설 정보 추가 (무장애 여행 시설 정보)
            TourApiResponseDto.TourBarrierFreeApiResponse barrierFreeDto =
                    getDetailWithBarrierFreeTour(spot.getContentId())
                            .blockOptional()
                            .orElse(null);
            if (barrierFreeDto != null) {
                List<SpotFacilityInformation> existingFacilityInfo = new ArrayList<>();
                List<FacilityInformation> facilityInfoList = getFacilityInfoList(barrierFreeDto);
                for (FacilityInformation facilityInformation : facilityInfoList) {
                    existingFacilityInfo.add(TourApiConverter.toSpotFacilityInformation(spot, facilityInformation));
                }
                spotFacilityInformationRepository.saveAll(existingFacilityInfo);
            }
        });
    }

    private List<FacilityInformation> getFacilityInfoList(TourApiResponseDto.TourBarrierFreeApiResponse barrierFreeDto) {
        if(barrierFreeDto.getResponse().getBody().getItems().getItem().isEmpty()) {
            return new ArrayList<>();
        }
        List<FacilityInfo> facilityInfoList = new ArrayList<>();
        var item = barrierFreeDto.getResponse().getBody().getItems().getItem().get(0);

        if (item.getParking().equals("")) {
            facilityInfoList.add(FacilityInfo.PARKING);
        }
        if (item.getElevator().equals("")) {
            facilityInfoList.add(FacilityInfo.ELEVATOR);
        }
        if (item.getExit().equals("")) {
            facilityInfoList.add(FacilityInfo.EXIT);
        }
        if (item.getWheelchair().equals("")) {
            facilityInfoList.add(FacilityInfo.WHEELCHAIR);
        }
        if (item.getRoute().equals("")) {
            facilityInfoList.add(FacilityInfo.ROUTE);
        }
        if (item.getRestroom().equals("")) {
            facilityInfoList.add(FacilityInfo.RESTROOM);
        }
        if (item.getStroller().equals("")) {
            facilityInfoList.add(FacilityInfo.STROLLER);
        }
        if (item.getLactationroom().equals("")) {
            facilityInfoList.add(FacilityInfo.LACTATION_ROOM);
        }
        List<FacilityInformation> getFacilityInformation = facilityInformationRepository.findAllByFacilityInfoIn(facilityInfoList);

        return getFacilityInformation;
    }

    /**
     * 지역코드 목록을 조회하는 메서드
     * @param areaCode (선택) 특정 지역의 시군구 코드를 조회하려면 상위 지역코드를 입력
     * @return AreaCodeResponseDto Mono 객체
     */
    private Mono<TourApiResponseDto.AreaCodeApiResponse> getAreaCode(Integer areaCode) {
        WebClient webClient = webClientBuilder
                .baseUrl(spotInfobaseUrl)
                .build();

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/areaCode2")
                        .queryParam("MobileOS", "AND") // MobileOS와 MobileApp은 필수값
                        .queryParam("MobileApp", "eromoro")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("_type", "json")   // 응답 타입을 JSON으로 지정
                        .queryParam("numOfRows", "100") // 한 번에 많은 데이터를 가져오기 위해 설정
                        .queryParamIfPresent("areaCode", java.util.Optional.ofNullable(areaCode)) // areaCode가 있을 때만 파라미터 추가
                        .build())
                .retrieve() // 응답을 받아옴
                .bodyToMono(TourApiResponseDto.AreaCodeApiResponse.class); // 응답 본문을 AreaCodeResponseDto 객체로 변환
    }

    /**
     * 지역 기반 관광정보 목록(areaBasedList2) 조회
     * @param areaCode     (선택) 시/도 코드
     * @param sigunguCode  (선택) 시/군/구 코드
     * @return Mono<TourListApiResponse>
     */
    private Mono<TourApiResponseDto.TourListApiResponse> getAreaBasedSpotList(
            Integer areaCode,
            Integer sigunguCode
    ) {
        WebClient wc = webClientBuilder
                .baseUrl(spotInfobaseUrl)
                .build();

        return wc.get()
                .uri(uri -> uri
                        .path("/areaBasedList2")
                        .queryParam("MobileOS", "AND")
                        .queryParam("MobileApp", "eromoro")
                        .queryParam("_type", "json")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("numOfRows", 30)
                        .queryParamIfPresent("areaCode",     java.util.Optional.ofNullable(areaCode))
                        .queryParamIfPresent("sigunguCode",  java.util.Optional.ofNullable(sigunguCode))
                        .build())
                .retrieve()
                .bodyToMono(TourApiResponseDto.TourListApiResponse.class);
    }

    private Mono<TourApiResponseDto.TourOverviewApiResponse>
    getDetailCommon(String contentId) {
//        // 필수 파라미터 가드
//        if (contentId == null || contentId.isBlank()) {
//            return Mono.error(new IllegalArgumentException("contentId is required"));
//        }
        WebClient wc = webClientBuilder.baseUrl(spotInfobaseUrl).build();

        return wc.get()
                .uri(uri -> uri
                        .path("/detailCommon2")
                        .queryParam("MobileOS", "AND")
                        .queryParam("MobileApp", "eromoro")
                        .queryParam("_type", "json")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("contentId", contentId)
                        .queryParam("numOfRows", 1)
                        .build())
                .retrieve()
                .bodyToMono(TourApiResponseDto.TourOverviewApiResponse.class);
    }

    private Mono<TourApiResponseDto.TourBarrierFreeApiResponse> getDetailWithBarrierFreeTour(String contentId) {
//        // 필수 파라미터 가드
//        if (contentId == null || contentId.isBlank()) {
//            return Mono.error(new IllegalArgumentException("contentId is required"));
//        }
        WebClient wc = webClientBuilder.baseUrl(barrierFreeTripBaseUrl).build();

        return wc.get()
                .uri(u -> u
                        .path("/detailWithTour2")
                        .queryParam("MobileOS", "AND")
                        .queryParam("MobileApp", "eromoro")
                        .queryParam("_type", "json")
                        .queryParam("serviceKey", serviceKey)
                        .queryParam("contentId", contentId)
                        .build())
                .retrieve()
                .bodyToMono(TourApiResponseDto.TourBarrierFreeApiResponse.class);
    }
}
