package com.suju.every_thing_with_spring.security.custom_user;

import com.suju.every_thing_with_spring.api.auth.db.AuthMapper;
import com.suju.every_thing_with_spring.api.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthMapper authMapper;
    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("loadUserByUsername");

        User user = authMapper.loadUserByUsername(username).orElseThrow(()
        -> new UsernameNotFoundException("User is not valid"));
        log.info("loadUserByUsername : {}",user);
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setUser(user);
        return customUserDetails;
    }
}
