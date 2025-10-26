package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.*;
import com.ermr.eromoro.domain.enums.ContentType;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.TourApiResponseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public class TourApiConverter {

    public static List<SidoArea> toSidoAreaList(Mono<TourApiResponseDto.AreaCodeApiResponse> responseMono) {
        return responseMono
                .map(response -> response.getResponse().getBody().getItems().getItem().stream()
                        .map(item -> SidoArea.builder()
                                .code(item.getCode())
                                .name(item.getName())
                                .build())
                        .toList())
                .block();
    }

    public static SiggArea toSiggArea(TourApiResponseDto.AreaCodeApiResponse.Item item) {
        return SiggArea.builder()
                .code(item.getCode())
                .name(item.getName())
                .build();
    }

    public static List<Spot> toSpot(SiggArea siggArea, Mono<TourApiResponseDto.TourListApiResponse> responseMono) {
        return responseMono
                .map(response -> response.getResponse().getBody().getItems().getItem().stream()
                        .filter(item -> {
                            String contentTypeId = item.getContenttypeid();
                            return contentTypeId.equals("12") || contentTypeId.equals("14") || contentTypeId.equals("15");
                        })
                        .map(item -> Spot.builder()
                                .siggArea(siggArea)
                                .title(item.getTitle())
                                .address(item.getAddr1() + " " + item.getAddr2())
                                .contentType(convertContentType(item.getContenttypeid(), item.getCat2()))
                                .imageUrl(item.getFirstimage())
                                .contentId(item.getContentid())
                                .build())
                        .toList())
                .block();
    }

    private static ContentType convertContentType(String contentTypeId, String cat2) {
        return switch (contentTypeId) {
            case "12" -> switch (cat2) {
                case "A0101" -> ContentType.NATURE;
                case "A0102" -> ContentType.TOURISM_RESOURCE;
                case "A0201" -> ContentType.HISTORICAL_SITE;
                case "A0202" -> ContentType.RESORT;
                case "A0203" -> ContentType.EXPERIENCE;
                case "A0204" -> ContentType.INDUSTRIAL;
                case "A0205" -> ContentType.ARCHITECTURE_SCULPTURE;
                default      -> ContentType.TOURIST_ATTRACTION;
            };
            case "14" -> ContentType.CULTURAL_FACILITY;
            case "15" -> ContentType.FESTIVAL;
//            case "25" -> ContentType.TRAVEL_COURSE;
//            case "28" -> ContentType.LEISURE_SPORTS;
//            case "32" -> ContentType.ACCOMMODATION;
//            case "38" -> ContentType.SHOPPING;
//            case "39" -> ContentType.RESTAURANT;
            default -> null;
        };
    }

    public static SpotFacilityInformation toSpotFacilityInformation(Spot spot, FacilityInformation facilityInformation) {
        return SpotFacilityInformation.builder()
                .spot(spot)
                .facilityInformation(facilityInformation)
                .build();
    }

//    public static FacilityInformation toFacilityInformation(TourApiResponseDto.TourBarrierFreeApiResponse barrierFreeDto) {
//        return FacilityInformation.builder()
//                .parkingInfo(barrierFreeDto.getParkingInfo())
//                .restroomInfo(barrierFreeDto.getRestroomInfo())
//                .useTime(barrierFreeDto.getUseTime())
//                .firstImageUrl(barrierFreeDto.getFirstImage())
//                .build();
//    }
}
