package com.suju.every_thing_with_spring.api.auth.service;

import com.suju.every_thing_with_spring.api.auth.web.dto.AuthDto;
import com.suju.every_thing_with_spring.api.auth.web.dto.LoginDto;
import com.suju.every_thing_with_spring.api.auth.web.dto.RefreshTokenDto;
import com.suju.every_thing_with_spring.api.auth.web.dto.RegisterDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    void register(RegisterDto registerDto);
    AuthDto login (LoginDto loginDto);
    AuthDto refreshToken (RefreshTokenDto token);
}
