package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.enums.IsAvailable;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public IsAvailable checkUsernameIsSame(UserRequestDTO.CheckUsernameIsSameDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return IsAvailable.UNAVAILABLE;
        } else {
            return  IsAvailable.AVAILABLE;
        }
    }
}
