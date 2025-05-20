package com.suju.every_thing_with_spring.api.auth.web;

import com.suju.every_thing_with_spring.api.auth.service.AuthService;
import com.suju.every_thing_with_spring.base.BaseRest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        authService.login(loginDto);
        return BaseRest.builder().build();
    }
}
