package com.ermr.eromoro.security.signin;

import com.ermr.eromoro.apiResponse.exception.handler.UserHandler;
import com.ermr.eromoro.apiResponse.status.ErrorStatus;
import com.ermr.eromoro.domain.User;
import com.ermr.eromoro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements CustomDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username, String password) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        org.springframework.security.core.userdetails.User securityUser = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );

        return securityUser;
    }
}
