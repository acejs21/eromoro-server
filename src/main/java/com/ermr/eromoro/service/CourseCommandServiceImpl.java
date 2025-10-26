package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.CourseHandler;
import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.CourseConverter;
import com.ermr.eromoro.converter.CoursePointConverter;
import com.ermr.eromoro.converter.LikeCourseConverter;
import com.ermr.eromoro.domain.*;
import com.ermr.eromoro.dto.CourseRequestDto;
import com.ermr.eromoro.dto.CourseResponseDto;
import com.ermr.eromoro.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseCommandServiceImpl implements CourseCommandService{

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CoursePointRepository coursePointRepository;
    private final LikeCourseRepository likeCourseRepository;
    private final SpotRepository spotRepository;

    @Override
    @Transactional
    public void startCourse(Long userId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        getUser.updateIsCourse();
    }

    @Override
    public User getIsCourseStatus(Long userId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        return getUser;
    }

    @Override
    public Course saveCoursePointList(Long userId, Long spotId, CourseRequestDto.CoursePointListDto request) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Course newCourse = Course.builder()
                .user(getUser)
                .title(getUser.getNickname() + "님의 코스")
                .likeCount(0)
                .duration(request.getDuration())
                .distance(request.getDistance())
                .rating(0)
                .isPublic(false)
                .build();
        if (spotId != null) {
            Spot getSpot = spotRepository.findById(spotId)
                    .orElseThrow(() -> new UserHandler(ErrorStatus.SPOT_NOT_FOUND));
            newCourse.setSpot(getSpot);
        }
        Course savedCourse = courseRepository.save(newCourse);
        List<CoursePoint> newCoursePointList = CoursePointConverter.toCoursePointList(request, newCourse);
        coursePointRepository.saveAll(newCoursePointList);

        return savedCourse;
    }

    @Override
    @Transactional
    public void saveCourseInfo(Long userId, Long courseId, CourseRequestDto.CourseInfoDto request) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Course getCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.COURSE_NOT_FOUND));
        if(!getCourse.getUser().equals(getUser)) {
            throw new UserHandler(ErrorStatus.COURSE_NOT_AUTHORIZED);
        }
        getCourse.updateCourseInfo(request.getTitle(), request.getIsPublic(), request.getRating());
    }

    @Override
    @Transactional
    public CourseResponseDto.updateLikeStatusResultDto updateLikeStatus(Long userId, Long courseId, boolean like) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Course getCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.COURSE_NOT_FOUND));
        boolean isLiked;

        LikeCourse getLikeCourse = likeCourseRepository.findByUserAndCourse(getUser, getCourse);
        if(like) {
            if (getLikeCourse != null) throw new CourseHandler(ErrorStatus.COURSE_ALREADY_LIKED);
            else {
                likeCourseRepository.save(LikeCourseConverter.toLikeCourse(getUser, getCourse));
                getCourse.updateLikeCount(1);
                isLiked = true;
            }
        } else {
            if (getLikeCourse == null) throw new CourseHandler(ErrorStatus.COURSE_NOT_LIKED);
            likeCourseRepository.delete(getLikeCourse);
            getCourse.updateLikeCount(-1);
            isLiked =  false;
        }

        return CourseConverter.toUpdateLikeStatusResultDto(isLiked, getCourse.getLikeCount());
    }

    @Override
    public void deleteCourse(Long userId, Long courseId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        Course getCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.COURSE_NOT_FOUND));
        if(!getCourse.getUser().equals(getUser)) {
            throw new UserHandler(ErrorStatus.COURSE_NOT_AUTHORIZED);
        }
        courseRepository.delete(getCourse);
    }
}
