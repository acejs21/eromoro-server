package com.ermr.eromoro.service;

import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.converter.UserConverter;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User signUp(UserRequestDTO.SignupDto request) {
        User newUser = UserConverter.toUser(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserHandler(ErrorStatus.SIGNUP_ALREADY_EXISTING_EMAIL);
        }

        return userRepository.save(newUser);
    }
}
