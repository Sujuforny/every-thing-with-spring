package com.suju.every_thing_with_spring.base;

import lombok.Builder;

import java.time.LocalDateTime;
 @Builder
public record BaseError<T>(Boolean status,
                           Integer code,
                           String message,
                           LocalDateTime timestamp,
                           T errors)  {
}
