package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Barrier;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.BarrierResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class BarrierConverter {

    public static BarrierResponseDto.GetAllBarrierDto toGetAllBarrierDto(List<Feedback> feedbacks, List<Barrier> barriers) {
        return BarrierResponseDto.GetAllBarrierDto.builder()
                .feedbacks(feedbacks.stream()
                        .map(f -> BarrierResponseDto.FeedbackDto.builder()
                                .feedbackId(f.getId())
                                .latitude(f.getLatitude())
                                .longitude(f.getLongitude())
                                .type(f.getType())
                                .build())
                        .collect(Collectors.toList()))
                .barriers(barriers.stream()
                        .map(b -> BarrierResponseDto.BarrierDto.builder()
                                .barrierId(b.getId())
                                .imageUrl(b.getImage())
                                .latitude(b.getLatitude())
                                .longitude(b.getLongitude())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
