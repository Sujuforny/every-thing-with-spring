package com.suju.every_thing_with_spring.api.auth.web;


import java.util.List;

public record RegisterDto (
        String emailOrUsername,
        String password,
        String confirmedPassword,
        List<Integer>roles
){
}
