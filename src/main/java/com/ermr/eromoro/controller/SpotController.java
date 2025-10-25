package com.ermr.eromoro.controller;

import com.ermr.eromoro.apiResponse.ApiResponse;
import com.ermr.eromoro.converter.CourseConverter;
import com.ermr.eromoro.domain.Course;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.jwt.SecurityUtil;
import com.ermr.eromoro.service.SpotCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spots")
@RequiredArgsConstructor
public class SpotController {

}
