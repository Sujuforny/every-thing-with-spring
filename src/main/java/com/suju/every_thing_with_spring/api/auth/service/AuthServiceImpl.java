package com.suju.every_thing_with_spring.api.auth.service;

import com.suju.every_thing_with_spring.api.auth.db.AuthMapper;
import com.suju.every_thing_with_spring.api.auth.web.RegisterDto;
import com.suju.every_thing_with_spring.api.user.User;
import com.suju.every_thing_with_spring.api.user.mapstruct.UserMapstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final UserMapstruct userMapstruct;
    private final AuthMapper authMapper;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public void register(RegisterDto registerDto) {
        User user = userMapstruct.registerDtoToUser(registerDto);
        log.info("User register information ==>{}", user.toString());
        user.setPassword(encoder.encode(user.getPassword()));
        var isRegistered =authMapper.register(user);
        log.info("isRegistered ==>{}", isRegistered);

        if(isRegistered){
            for(Integer role : registerDto.roles()){
                log.info("user.getId() ==>{}", user.getId());
                log.info("user role ==>{}", role);

                authMapper.createUserRoles(user.getId(),role);
            }
        }

    }
}
