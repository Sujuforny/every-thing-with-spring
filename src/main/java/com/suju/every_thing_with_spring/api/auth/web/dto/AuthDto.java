package com.suju.every_thing_with_spring.api.auth.web.dto;

public record AuthDto( String tokenType,String accessToken,String refreshToken) {
}
