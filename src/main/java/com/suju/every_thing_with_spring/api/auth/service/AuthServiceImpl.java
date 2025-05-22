package com.suju.every_thing_with_spring.api.auth.service;

import com.nimbusds.jwt.JWTClaimsSet;
import com.suju.every_thing_with_spring.api.auth.db.AuthMapper;
import com.suju.every_thing_with_spring.api.auth.web.AuthDto;
import com.suju.every_thing_with_spring.api.auth.web.LoginDto;
import com.suju.every_thing_with_spring.api.auth.web.RegisterDto;
import com.suju.every_thing_with_spring.api.user.User;
import com.suju.every_thing_with_spring.api.user.mapstruct.UserMapstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final UserMapstruct userMapstruct;
    private final AuthMapper authMapper;
    private final PasswordEncoder encoder;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final JwtEncoder jwtEncoder;
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

    @Override
    public AuthDto login(LoginDto loginDto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginDto.username(),loginDto.password());
        Authentication auth = daoAuthenticationProvider.authenticate(authentication);
        log.info("Authentication:{}",auth);

        //create time now
        Instant now = Instant.now();

        //Define scope
        String scope = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .claim("scope",scope)
                .build();
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();

        return new AuthDto("Bearer",accessToken);
    }
}
