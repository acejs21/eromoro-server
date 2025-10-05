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
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User signUp(UserRequestDTO.SignUpDto request) {
        User newUser = UserConverter.toUser(request);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserHandler(ErrorStatus.SIGNUP_ALREADY_EXISTING_ACCOUNT);
        }

        return userRepository.save(newUser);
    }
}
