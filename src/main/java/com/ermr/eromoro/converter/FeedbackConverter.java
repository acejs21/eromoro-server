package com.ermr.eromoro.converter;

import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.FeedbackRequestDto;
import com.ermr.eromoro.dto.FeedbackResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackConverter {

    public static Feedback toFeedback(FeedbackRequestDto.CreateDto request) {

        return Feedback.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .photo(request.getPhoto())
                .type(request.getType())
                .isReport(request.isReport())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }

    public static FeedbackResponseDto.CreateResultDto toCreateResultDTO(Feedback feedback){
        return FeedbackResponseDto.CreateResultDto.builder()
                .feedbackId(feedback.getId())
                .build();
    }

    public static FeedbackResponseDto.DetailResultDto toDetailResultDto(Feedback feedback) {
        return FeedbackResponseDto.DetailResultDto.builder()
                .feedbackId(feedback.getId())
                .userId(feedback.getUserId())
                .emdAreaId(feedback.getEmdAreaId())
                .title(feedback.getTitle())
                .content(feedback.getContent())
                .type(feedback.getType())
                .isReport(feedback.isReport())
                .latitude(feedback.getLatitude())
                .longitude(feedback.getLongitude())
                .likeCount(feedback.getLikeCount())
                .dislikeCount(feedback.getDislikeCount())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }

    public static FeedbackResponseDto.MyFeedbacksResultDto toMyFeedbacksResultDto(List<Feedback> feedbacks) {
        List<FeedbackResponseDto.MyFeedbacksResultDto.MyFeedbackDto> feedbackDtos = feedbacks.stream()
                .map(f -> FeedbackResponseDto.MyFeedbacksResultDto.MyFeedbackDto.builder()
                        .feedbackId(f.getId())
                        .isReport(f.isReport())
                        .emdAreaId(f.getEmdAreaId())
                        .title(f.getTitle())
                        .type(f.getType())
                        .updatedAt(f.getUpdatedAt())
                        .likeCount(f.getLikeCount())
                        .dislikeCount(f.getDislikeCount())
                        .build())
                .collect(Collectors.toList());

        return FeedbackResponseDto.MyFeedbacksResultDto.builder()
                .feedbacks(feedbackDtos)
                .totalCount(feedbackDtos.size())
                .build();
    }
}
