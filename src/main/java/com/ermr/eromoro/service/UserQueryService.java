package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.enums.IsAvailable;
import com.ermr.eromoro.dto.UserRequestDTO;
import com.ermr.eromoro.dto.UserResponseDTO;

public interface UserQueryService {

    IsAvailable checkUsernameIsSame(UserRequestDTO.CheckUsernameIsSameDTO request);
}
