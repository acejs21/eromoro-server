package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.dto.BarrierRequestDto;
import com.ermr.eromoro.jwt.SecurityUtil;
import com.ermr.eromoro.service.BarrierCommandService;
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
}
