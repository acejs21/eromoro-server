package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.UserConverter;
import com.ermr.eromoro.domain.LikeCourse;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.domain.enums.IsAvailable;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;
import com.ermr.eromoro.repository.CourseRepository;
import com.ermr.eromoro.repository.LikeCourseRepository;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LikeCourseRepository likeCourseRepository;

    @Override
    public IsAvailable checkUsernameIsSame(UserRequestDTO.CheckUsernameIsSameDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return IsAvailable.UNAVAILABLE;
        } else {
            return  IsAvailable.AVAILABLE;
        }
    }

    @Override
    public User getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
    }

    @Override
    public UserResponseDTO.GetMyPageInfoResultDTO getMyPageInfo(Long userId) {
        User getUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
        int courseCount = courseRepository.countByUser(getUser);
        List<LikeCourse> getLikeCourseList = likeCourseRepository.findByUser(getUser);

        return UserConverter.toGetMyPageInfoResultDTO(getUser, courseCount, getLikeCourseList);
    }
}
