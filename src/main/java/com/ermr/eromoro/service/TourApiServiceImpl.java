package com.ermr.eromoro.service;

import com.ermr.eromoro.converter.TourApiConverter;
import com.ermr.eromoro.domain.SidoArea;
import com.ermr.eromoro.dto.TourApiResponseDto;
import com.ermr.eromoro.repository.SidoAreaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourApiServiceImpl implements TourApiService {

    @Value("${tour-api.service-key}")
    private String serviceKey;
    private final WebClient webClient;
    private final SidoAreaRepository sidoAreaRepository;

    @Override
    public void collectSidoArea() {
        List<SidoArea> sidoAreaList = TourApiConverter.toSidoAreaList(getAreaCode(null));
        for(SidoArea sidoArea : sidoAreaList) {
            System.out.println("지역코드: " + sidoArea.getCode());
            System.out.println("지역이름: " + sidoArea.getName());
            System.out.println("-----------------------");
        }
        sidoAreaRepository.saveAll(sidoAreaList);
    }

    /**
     * 지역코드 목록을 조회하는 메서드
     * @param areaCode (선택) 특정 지역의 시군구 코드를 조회하려면 상위 지역코드를 입력
     * @return AreaCodeResponseDto Mono 객체
     */
    private Mono<TourApiResponseDto.AreaCodeApiResponse> getAreaCode(String areaCode) {
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
