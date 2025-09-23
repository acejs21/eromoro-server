package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.FeedbackConverter;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.FeedbackRequestDto;
import com.ermr.eromoro.dto.FeedbackResponseDto;
import com.ermr.eromoro.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ApiResponse<FeedbackResponseDto.CreateResultDto> create(
            @RequestBody FeedbackRequestDto.CreateDto request
    ){
        Feedback feedback = feedbackService.create(request);
        return ApiResponse.onSuccess(FeedbackConverter.toCreateResultDTO(feedback));
    }

    @GetMapping("/{feedback_id}")
    public ApiResponse<FeedbackResponseDto.DetailResultDto> getDetail(
            @PathVariable("feedback_id") Long feedbackId
    ) {
        Feedback feedback = feedbackService.findById(feedbackId);
        return ApiResponse.onSuccess(FeedbackConverter.toDetailResultDto(feedback));
    }

    @GetMapping("/my")
    public ApiResponse<FeedbackResponseDto.MyFeedbacksResultDto> getMyFeedbacks() {
        // 임시 하드 코드 값 사용
        Long userId = 1L;
        List<Feedback> feedbacks = feedbackService.getMyFeedbacks(userId);
        return ApiResponse.onSuccess(FeedbackConverter.toMyFeedbacksResultDto(feedbacks));
    }

    @PatchMapping("/{feedback_id}")
    public ApiResponse<FeedbackResponseDto.DetailResultDto> updateFeedback(
            @PathVariable("feedback_id") Long feedbackId,
            @RequestBody FeedbackRequestDto.UpdateDto request
    ) {
        Feedback updated = feedbackService.update(feedbackId, request);
        return ApiResponse.onSuccess(FeedbackConverter.toDetailResultDto(updated));
    }

    @DeleteMapping("/{feedback_id}")
    public ApiResponse<LocalDateTime> deleteFeedback(
            @PathVariable("feedback_id") Long feedbackId
    ) {
        LocalDateTime deletedAt = feedbackService.delete(feedbackId);
        return ApiResponse.onSuccess(deletedAt);
    }

    //getFeedbacksByArea
    //
}
