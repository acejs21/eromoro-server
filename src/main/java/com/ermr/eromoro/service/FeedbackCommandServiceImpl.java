package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.FeedbackHandler;
import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.FeedbackConverter;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.FeedbackRequestDto;
import com.ermr.eromoro.repository.FeedbackRepository;
import com.ermr.eromoro.repository.UserRepository;
import com.ermr.eromoro.util.NcpObjectStorageClient;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackCommandServiceImpl implements FeedbackCommandService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final NcpObjectStorageClient storage;

    @Override
    public Feedback create(FeedbackRequestDto.CreateDto request, MultipartFile image, Long userId) {
        User getUser = userRepository.findById(userId) // 후에 null처리하기 (2025/10/06)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Feedback newFeedback = FeedbackConverter.toFeedback(request);
        newFeedback.setUser(getUser);

        // 이미지 저장
        String contentType = image.getContentType() != null ? image.getContentType() : "application/octet-stream";
        String ext = extractExt(image.getOriginalFilename());
        String key = buildKey(userId, ext);
        try {
            storage.putObject(key, image.getInputStream(), image.getSize(), contentType);
            newFeedback.setPhotoUrl(storage.publicUrl(key));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return feedbackRepository.save(newFeedback);
    }

    @Override
    @Transactional
    public Feedback update(Long feedbackId, FeedbackRequestDto.UpdateDto request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found: " + feedbackId));

        if (request.getTitle() != null) { feedback.setTitle(request.getTitle()); }
        if (request.getContent() != null) { feedback.setContent(request.getContent()); }
        if (request.getType() != null) { feedback.setType(request.getType()); }
        if (request.getIsReport()) { feedback.setIsReport(true); }
        if (request.getLatitude() != null) { feedback.setLatitude(request.getLatitude()); }
        if (request.getLongitude() != null) { feedback.setLongitude(request.getLongitude()); }

        return feedbackRepository.save(feedback);
    }

    @Override
    public LocalDateTime delete(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new FeedbackHandler(ErrorStatus.FEEDBACK_NOT_FOUND));
        // 권한 없음 에러 추가

        feedbackRepository.delete(feedback);
        return LocalDateTime.now();
    }

    // 이미지 저장 util 메소드
    private String extractExt(String name) {
        if (name == null) return "bin";
        int dot = name.lastIndexOf('.');
        return (dot > -1 && dot < name.length() - 1) ? name.substring(dot + 1) : "bin";
    }

    private String buildKey(Long userId, String ext) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return "photos/" + userId + "/" + date + "/" + System.nanoTime() + "." + ext;
    }

}
