package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.FeedbackConverter;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.FeedbackRequestDto;
import com.ermr.eromoro.dto.FeedbackResponseDto;
import com.ermr.eromoro.jwt.SecurityUtil;
import com.ermr.eromoro.service.FeedbackCommandService;
import com.ermr.eromoro.service.FeedbackQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedbacks")
public class FeedbackController {

    private final FeedbackCommandService feedbackCommandService;
    private final FeedbackQueryService feedbackQueryService;

    @Operation(summary = "피드백 작성 [v1.0 (2025-10-06)]", description =
            "# [v1.0 (2025-10-06)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b2072803fb0a6f0b850e9185f&pm=s)" +
                    " 피드백 작성 API 입니다."
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<FeedbackResponseDto.CreateResultDto> create(
            @RequestPart(value = "request") FeedbackRequestDto.CreateDto request,
            @RequestPart(value = "photo", required = false) MultipartFile image
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        Feedback feedback = feedbackCommandService.create(request, image, userId);
        return ApiResponse.onSuccess(FeedbackConverter.toCreateResultDTO(feedback));
    }

    @Operation(summary = "특정 피드백 상세 조회 [v1.1 (2025-10-23)]", description =
            "# [v1.1 (2025-10-23)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b207280538e15ca37f7d82599&pm=s)" +
                    " 특정 피드백 상세 조회 API 입니다."
    )
    @GetMapping("/{feedback-id}")
    public ApiResponse<FeedbackResponseDto.DetailResultDto> getDetailFeedback(
            @PathVariable("feedback-id") Long feedbackId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.onSuccess(feedbackQueryService.getDetailFeedback(userId, feedbackId));
    }

    @Operation(summary = "내 피드백 목록 조회 [v1.1 (2025-10-23)]", description =
            "# [v1.1 (2025-10-23)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2755335b207280448464edc18316ac25&pm=s)" +
                    " 내 피드백 목록 조회 API 입니다."
    )
    @GetMapping("/my")
    public ApiResponse<FeedbackResponseDto.MyFeedbacksResultDto> getMyFeedbacks() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.onSuccess(feedbackQueryService.getMyFeedbacks(userId));
    }

    // 피드백 수정 삭제
//    @PatchMapping("/{feedback_id}")
//    public ApiResponse<FeedbackResponseDto.DetailResultDto> updateFeedback(
//            @PathVariable("feedback_id") Long feedbackId,
//            @RequestBody FeedbackRequestDto.UpdateDto request
//    ) {
//        Feedback updated = feedbackCommandService.update(feedbackId, request);
//        return ApiResponse.onSuccess(FeedbackConverter.toDetailResultDto(updated));
//    }

    @Operation(summary = "피드백 삭제 [v1.0 (2025-10-08)]", description =
            "# [v1.0 (2025-10-08)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b207280eaab5bcbb2ef096293&pm=s)" +
                    " 피드백 삭제 API 입니다."
    )
    @DeleteMapping("/{feedback_id}")
    public ApiResponse<?> deleteFeedback(
            @PathVariable("feedback_id") Long feedbackId
    ) {
        LocalDateTime deletedAt = feedbackCommandService.delete(feedbackId);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "피드백 좋아요 ON/OFF [v1.0 (2025-10-23)]", description =
            "# [v1.0 (2025-10-23)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2885335b2072803ea2d6c588b557b4d8&pm=s)" +
                    " 피드백 좋아요 누름/취소. 좋아요를 ON 시키고자 한다면 Query String으로 like = true, 좋아요를 OFF 시키고자 한다면 Query String으로 like = false를 넣어주세요. \n" +
                    "좋아요 활성화 여부와 좋아요 개수를 반환합니다."
    )
    @PostMapping("/{feedback-id}/like")
    public ApiResponse<FeedbackResponseDto.updateLikeStatusResultDto> updateLikeStatus(
            @PathVariable("feedback-id") Long feedbackId,
            @RequestParam boolean like
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.onSuccess(feedbackCommandService.updateLikeStatus(userId, feedbackId, like));
    }

    //getFeedbacksByArea
    //
}
