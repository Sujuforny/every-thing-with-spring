package com.suju.every_thing_with_spring.api.auth.service;

import com.suju.every_thing_with_spring.api.auth.web.RegisterDto;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    void register(RegisterDto registerDto);
}
