package com.ermr.eromoro.service;

import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.dto.UserRequestDTO;

public interface UserCommandService {

    User signUp(UserRequestDTO.SignUpDto request);
}
