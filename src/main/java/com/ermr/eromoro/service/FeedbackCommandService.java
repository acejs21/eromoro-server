package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.FeedbackHandler;
import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.FeedbackConverter;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.FeedbackRequestDto;
import com.ermr.eromoro.dto.FeedbackResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedbackCommandService {

    Feedback create(FeedbackRequestDto.CreateDto request, MultipartFile image, Long userId);
//    Feedback update(Long feedbackId, FeedbackRequestDto.UpdateDto request);
    LocalDateTime delete(Long feedbackId);
    FeedbackResponseDto.updateLikeStatusResultDto updateLikeStatus(Long userId, Long feedbackId, boolean like);
}
