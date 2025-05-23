package com.suju.every_thing_with_spring.api.auth.web;

import com.suju.every_thing_with_spring.api.auth.service.AuthService;
import com.suju.every_thing_with_spring.api.auth.web.dto.AuthDto;
import com.suju.every_thing_with_spring.api.auth.web.dto.LoginDto;
import com.suju.every_thing_with_spring.api.auth.web.dto.RefreshTokenDto;
import com.suju.every_thing_with_spring.api.auth.web.dto.RegisterDto;
import com.suju.every_thing_with_spring.base.BaseRest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestController {
    private final AuthService authService;
    @PostMapping("/register")
    public BaseRest<?> register(@RequestBody RegisterDto registerDto){
        authService.register(registerDto);
        return BaseRest.builder().build();
    }

    @PostMapping("/login")
    public BaseRest<?> login(@RequestBody LoginDto loginDto){
        AuthDto authDto = authService.login(loginDto);
        return BaseRest.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .message("Login successful")
                .timestamp(LocalDateTime.now())
                .data(authDto)
                .build();
    }

    @PostMapping("/refresh-token")
    public BaseRest<?> refreshToken(@RequestBody RefreshTokenDto refreshToken){
        AuthDto authDto = authService.refreshToken(refreshToken);
        return BaseRest.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .message("Login successful")
                .timestamp(LocalDateTime.now())
                .data(authDto)
                .build();
    }
}
