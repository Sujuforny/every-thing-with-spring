package com.suju.every_thing_with_spring.api.user.web;

import com.suju.every_thing_with_spring.base.BaseRest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {
    @GetMapping
    public BaseRest<?> findAllUsers(){
        return BaseRest.builder()
                .status(true)
                .code(HttpStatus.OK.value())
                .message("User have been found!")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
