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
//                .photoUrl() <- Object Storage 연동 후 처리
                .type(request.getType())
                .isReport(request.getIsReport())
                .accepted(false) // 기본값 false
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .likeCount(0)
                .dislikeCount(0)
                .build();
    }

    public static FeedbackResponseDto.CreateResultDto toCreateResultDTO(Feedback feedback){
        return FeedbackResponseDto.CreateResultDto.builder()
                .feedbackId(feedback.getId())
                .build();
    }

    public static FeedbackResponseDto.DetailResultDto toDetailResultDto(Feedback feedback, boolean isLiked) {
        return FeedbackResponseDto.DetailResultDto.builder()
                .feedbackId(feedback.getId())
//                .emdAreaId(feedback.getEmdAreaId())      // 수정(2025/10/06
                .title(feedback.getTitle())
                .content(feedback.getContent())
                .photoUrl(feedback.getPhotoUrl())
                .type(feedback.getType())
                .isReport(feedback.getIsReport())
                .latitude(feedback.getLatitude())
                .longitude(feedback.getLongitude())
                .address(feedback.getAddress())
                .likeCount(feedback.getLikeCount())
                .dislikeCount(feedback.getDislikeCount())
                .isLiked(isLiked)
                .createdAt(feedback.getCreatedAt())
                .build();
    }

    public static FeedbackResponseDto.MyFeedbacksResultDto toMyFeedbacksResultDto(List<FeedbackResponseDto.MyFeedbackDto> myFeedbackDtoList) {
        return FeedbackResponseDto.MyFeedbacksResultDto.builder()
                .feedbacks(myFeedbackDtoList)
                .build();
    }

//    public static FeedbackResponseDto.MyFeedbacksResultDto toMyFeedbacksResultDto(List<Feedback> feedbacks) {
//        List<FeedbackResponseDto.MyFeedbackDto> feedbackDtos = feedbacks.stream()
//                .map(f -> FeedbackResponseDto.MyFeedbackDto.builder()
//                        .feedbackId(f.getId())
//                        .photoUrl(f.getPhotoUrl())
////                        .emdAreaId(f.getEmdAreaId())
//                        .title(f.getTitle())
//                        .address(f.getAddress())
//                        .type(f.getType())
//                        .createdAt(f.getCreatedAt())
//                        .likeCount(f.getLikeCount())
//                        .dislikeCount(f.getDislikeCount())
//                        .isReport(f.getIsReport())
//                        .accepted(f.getAccepted())
//                        .build())
//                .collect(Collectors.toList());
//
//        return FeedbackResponseDto.MyFeedbacksResultDto.builder()
//                .feedbacks(feedbackDtos)
////                .totalCount(feedbackDtos.size())
//                .build();
//    }

    public static FeedbackResponseDto.updateLikeStatusResultDto toUpdateLikeStatusResultDto(boolean isLiked, int likeCount) {
        return FeedbackResponseDto.updateLikeStatusResultDto.builder()
                .isLiked(isLiked)
                .likeCount(likeCount)
                .build();
    }
}
