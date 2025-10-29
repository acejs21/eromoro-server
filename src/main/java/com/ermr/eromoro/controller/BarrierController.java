package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.BarrierConverter;
import com.ermr.eromoro.converter.CourseConverter;
import com.ermr.eromoro.domain.Barrier;
import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.domain.CoursePoint;
import com.ermr.eromoro.domain.Feedback;
import com.ermr.eromoro.dto.BarrierRequestDto;
import com.ermr.eromoro.dto.BarrierResponseDto;
import com.ermr.eromoro.dto.CourseResponseDto;
import com.ermr.eromoro.jwt.SecurityUtil;
import com.ermr.eromoro.service.BarrierCommandService;
import com.ermr.eromoro.service.BarrierQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/barriers")
@RequiredArgsConstructor
public class BarrierController {

    private final BarrierCommandService barrierCommandService;
    private final BarrierQueryService barrierQueryService;

    @Operation(summary = "[ 백엔드 개발자용 ] 계단 데이터 수집 및 저장 API [v1.0 (2025-10-19)]", description =
            "# [v1.0 (2025-10-19)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2915335b207280b2a363d7a04a21eff9&pm=s)" +
                    " 계단 데이터 수집 및 저장 API 입니다. 코스 지점들(리스트)을 저장합니다."
    )
    @PostMapping(
            value = "/step",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<?> collectAndSaveStepData(
            @RequestPart("photos") List<MultipartFile> photos,
            @RequestPart(value = "request", required = false) List<BarrierRequestDto.StepDataPhotoMetaDataDto> request // JSON 메타 병행 전송 시
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        barrierCommandService.collectAndSaveStepData(userId, photos, request);

        // 각 파일 정보
        if (photos != null) {
            for (int i = 0; i < photos.size(); i++) {
                MultipartFile f = photos.get(i);
                System.out.println(
                        "photo[" + i + "] " +
                                "partName=" + f.getName() + ", " +
                                "originalName=" + f.getOriginalFilename() + ", " +
                                "size=" + f.getSize() + ", " +
                                "contentType=" + f.getContentType() + ", " +
                                "empty=" + f.isEmpty()
                );
            }
        }

        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "장애물 전체 조회 API 입니다. [v1.0 (2025-10-29)]", description =
            "# [v1.0 (2025-10-29)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2995335b20728033aa68f51adbd58b46&pm=s)" +
                    " 장애물 전체 조회 API 입니다."
    )
    @GetMapping("/all")
    public ApiResponse<BarrierResponseDto.GetAllBarrierDto> getAllBarrier() {
        Long userId = SecurityUtil.getCurrentUserId();
        barrierQueryService.checkAuthorization(userId);
        List<Feedback> feedbacks = barrierQueryService.getAllFeedbacks();
        List<Barrier> barriers = barrierQueryService.getAllBarriers();
        return ApiResponse.onSuccess(BarrierConverter.toGetAllBarrierDto(feedbacks, barriers));
    }

    @Operation(summary = "장애물 지도 페이징 조회 API 입니다. [v1.0 (2025-10-29)]", description =
            "# [v1.0 (2025-10-19)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=29a5335b207280f2aab2dc44e7a72364&pm=s)" +
                    " 장애물 지도 페이징 조회 API 입니다. 좌측하단좌표부터 반시계방향으로 위치좌표를 입력해주세요."
    )
    @PostMapping("/paging")
    public ApiResponse<BarrierResponseDto.GetAllBarrierDto> getPagingBarrier(
            @RequestBody BarrierRequestDto.GetPagingBarrierDto request
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        barrierQueryService.checkAuthorization(userId);
        List<Feedback> feedbacks = barrierQueryService.getPagingFeedbacks(request);
        List<Barrier> barriers = barrierQueryService.getPagingBarriers(request);
        return ApiResponse.onSuccess(BarrierConverter.toGetAllBarrierDto(feedbacks, barriers));
    }
}
