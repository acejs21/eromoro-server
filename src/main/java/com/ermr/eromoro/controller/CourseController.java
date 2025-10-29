package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.CourseConverter;
import com.ermr.eromoro.domain.*;
import com.ermr.eromoro.domain.enums.CourseType;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.CourseResponseDto;
import com.ermr.eromoro.jwt.SecurityUtil;
import com.ermr.eromoro.service.CourseCommandService;
import com.ermr.eromoro.service.CourseQueryService;
import com.ermr.eromoro.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseCommandService courseCommandService;
    private final CourseQueryService courseQueryService;
    private final CourseService courseService;

    @Operation(summary = "코스 시작 [v1.0 (2025-10-10)]", description =
            "# [v1.0 (2025-10-10)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b207280d680e3dadb83f6f52e&pm=s)" +
                    " 코스 시작 API 입니다. 회원의 코스 시작 status를 true로 바꿉니다."
    )
    @PatchMapping("/{course-id}")
    public ApiResponse<?> startCourse(@PathVariable("course-id") Long courseId){
        Long userId = SecurityUtil.getCurrentUserId();
        courseCommandService.startCourse(userId, courseId);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "현재 진행중인 코스 확인 [v1.0 (2025-10-10)]", description =
            "# [v1.0 (2025-10-10)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b207280b5a92dc0f9be9f9e57&pm=s)" +
                    " 현재 진행중인 코스 확인 API 입니다. 회원의 코스 시작 status를 반환합니다."
    )
    @GetMapping("/current")
    public ApiResponse<?> getIsCourseStatus(){
        Long userId = SecurityUtil.getCurrentUserId();
        User getUser = courseCommandService.getIsCourseStatus(userId);
        return ApiResponse.onSuccess(CourseConverter.toIsCourseResultDto(getUser));
    }

    @Operation(summary = "코스 지점 저장 [v1.0 (2025-10-10)]", description =
            "# [v1.0 (2025-10-10)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b207280c3829dd7071ebe67a6&pm=s)" +
                    " 코스 지점 저장 API 입니다. 코스 지점들(리스트)을 저장합니다."
    )
    @PostMapping("/point")
    public ApiResponse<?> saveCoursePointList(
            @RequestParam(required = false) Long spotId,
            @RequestBody CourseRequestDto.CoursePointListDto request
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        Course savedCourse = courseCommandService.saveCoursePointList(userId, spotId, request);
        return ApiResponse.onSuccess(CourseConverter.toSaveCoursePointResultDto(savedCourse));
    }

    @Operation(summary = "코스 정보 저장 [v1.0 (2025-10-13)]", description =
            "# [v1.0 (2025-10-13)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b207280dfb5d8c099b32315fd&pm=s)" +
                    " 코스 정보를 저장하는 API 입니다. 코스 이름, 공개 여부, 별점을 저장합니다."
    )
    @PatchMapping("/info/{courseId}")
    public ApiResponse<?> saveCourseInfo(
            @PathVariable Long courseId,
            @RequestBody CourseRequestDto.CourseInfoDto request
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        courseCommandService.saveCourseInfo(userId, courseId, request);
        return ApiResponse.onSuccess("");
    }

    @Operation(summary = "내 코스/내가 좋아요 누른 코스/관광지 사용자 공개 코스 목록 검색 조회 [v1.0 (2025-10-14)]", description =
            "# [v1.0 (2025-10-14)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2645335b2072809aa8f4ce4fbb33dd9d&pm=s)" +
                    " 내 코스/내가 좋아요 누른 코스/관광지 사용자 공개 코스 목록 검색 조회 API 입니다. Query String에 코스의 타입 또는 검색어 또는 관광지 ID를 입력해주세요."
    )
    @GetMapping("/current/list")
    public ApiResponse<CourseResponseDto.getCourseListResultDto> getCourseList(
            @RequestParam(required=false) CourseType courseType,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) Long spotId
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.onSuccess(courseQueryService.getCourseList(userId, courseType, keyword, spotId));
    }

    @Operation(summary = "코스 좋아요 ON/OFF [v1.0 (2025-10-21)]", description =
            "# [v1.0 (2025-10-10)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2885335b2072808d9e41fd801608af22&pm=s)" +
                    " 코스 좋아요 누름/취소 API 입니다. 좋아요를 ON 시키고자 한다면 Query String으로 like = true, 좋아요를 OFF 시키고자 한다면 Query String으로 like = false를 넣어주세요. \n" +
                    "좋아요 활성화 여부와 좋아요 개수를 반환합니다."
    )
    @PostMapping("/{courseId}/like")
    public ApiResponse<CourseResponseDto.updateLikeStatusResultDto> updateLikeStatus(
            @PathVariable Long courseId,
            @RequestParam boolean like
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        return ApiResponse.onSuccess(courseCommandService.updateLikeStatus(userId, courseId, like));
    }

    @Operation(summary = "코스 삭제 [v1.0 (2025-10-21)]", description =
            "# [v1.0 (2025-10-10)](https://www.notion.so/API-24b5335b2072801abf1ee6036c06b00e?p=2885335b2072808d9e41fd801608af22&pm=s)" +
                    " 코스 삭제 API 입니다. 삭제할 코스 ID를 Path Variable로 넣어주세요."
    )
    @DeleteMapping("/{courseId}")
    public ApiResponse<?> deleteCourse(
            @PathVariable Long courseId
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        courseCommandService.deleteCourse(userId, courseId);
        return ApiResponse.onSuccess("");
    }

    @PostMapping("/generate")
    public ApiResponse<CourseResponseDto.GenerateResultDto> generate(
            @RequestBody CourseRequestDto.GenerateDto request
    ){
        Long userId = SecurityUtil.getCurrentUserId();
        List<Course> courseList = courseService.generateCourse(request, userId);
        return ApiResponse.onSuccess(CourseConverter.toGenerateResultDto(courseList));
    }

    @GetMapping("/{course_id}")
    public ApiResponse<CourseResponseDto.CourseDetailDto> getCourseDetail(
            @PathVariable("course_id") Long courseId
    ) {
        Course course = courseService.getCourse(courseId);
        List<CoursePoint> points = courseService.getCoursePointsOrdered(courseId);
//        List<Feedback> feedbacks = courseService.getCourseFeedbacks(points);
//        List<Barrier> barriers = courseService.getCourseBarriers(points);
        return ApiResponse.onSuccess(CourseConverter.toCourseDetailDto(course, points));
    }
}
