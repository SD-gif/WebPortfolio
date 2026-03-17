package com.seodong.portfolio.admin;

import com.seodong.portfolio.admin.dto.LoginRequest;
import com.seodong.portfolio.admin.dto.LoginResponse;
import com.seodong.portfolio.common.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        return new LoginResponse(jwtProvider.generateToken(auth.getName()));
    }
}
