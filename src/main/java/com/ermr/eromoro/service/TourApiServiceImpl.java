package com.ermr.eromoro.service;

import com.ermr.eromoro.converter.TourApiConverter;
import com.ermr.eromoro.domain.SidoArea;
import com.ermr.eromoro.domain.SiggArea;
import com.ermr.eromoro.dto.TourApiResponseDto;
import com.ermr.eromoro.repository.SidoAreaRepository;
import com.ermr.eromoro.repository.SiggAreaRepository;
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
    private final WebClient webClient;
    private final SidoAreaRepository sidoAreaRepository;
    private final SiggAreaRepository siggAreaRepository;

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

    /**
     * 지역코드 목록을 조회하는 메서드
     * @param areaCode (선택) 특정 지역의 시군구 코드를 조회하려면 상위 지역코드를 입력
     * @return AreaCodeResponseDto Mono 객체
     */
    private Mono<TourApiResponseDto.AreaCodeApiResponse> getAreaCode(Integer areaCode) {
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
}
