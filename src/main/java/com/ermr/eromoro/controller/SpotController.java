package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.CourseConverter;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.ContentType;
import com.ermr.eromoro.dto.SpotRequestDto;
import com.ermr.eromoro.dto.SpotResponseDto;
import com.ermr.eromoro.jwt.SecurityUtil;
import com.ermr.eromoro.service.SpotQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spots")
@RequiredArgsConstructor
public class SpotController {

    private  final SpotQueryService courseCommandService;

    @Operation(summary = "관광지 목록 조회 [v1.0 (2025-10-25)]", description =
            "# [v1.0 (2025-10-25)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b2072805183fee1f9b1636302&pm=s)" +
                    " 관광지 목록 조회 API 입니다."
    )
    @GetMapping("/list")
    public ApiResponse<SpotResponseDto.GetSpotListDto> getSpotList(
            @RequestParam ContentType category,
            @RequestParam(required = false) String siGunGu,
            @RequestParam(required = false) String keyword,
            @RequestParam int pageNum
    ){
        Long userId = SecurityUtil.getCurrentUserId();;
        return ApiResponse.onSuccess(courseCommandService.getSpotList(category, siGunGu, keyword, pageNum));
    }

    @Operation(summary = "특정 관광지 세부조회 [v1.0 (2025-10-26)]", description =
            "# [v1.0 (2025-10-26)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b207280899473f72c4194058c&pm=s)" +
                    " 특정 관광지 세부조회 API 입니다."
    )
    @GetMapping("/{spot-id}")
    public ApiResponse<SpotResponseDto.GetSpotDetailDto> getSpotDetail(
            @PathVariable("spot-id") Long spotId
    ){
        Long userId = SecurityUtil.getCurrentUserId();;
        return ApiResponse.onSuccess(courseCommandService.getSpotDetail(userId, spotId));
    }
}
