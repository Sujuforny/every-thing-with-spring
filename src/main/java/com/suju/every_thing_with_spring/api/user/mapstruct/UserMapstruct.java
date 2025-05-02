package com.suju.every_thing_with_spring.api.user.mapstruct;

import com.suju.every_thing_with_spring.api.auth.web.RegisterDto;
import com.suju.every_thing_with_spring.api.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapstruct {
    UserMapstruct INSTANCE = Mappers.getMapper(UserMapstruct.class);
    @Mapping(target = "email", source = "emailOrUsername", qualifiedByName = "mapToEmail")
    @Mapping(target = "username", source = "emailOrUsername", qualifiedByName = "mapToUsername")
    User registerDtoToUser(RegisterDto registerDto);

    // Custom method to map emailOrUsername to email if it contains "@"
    @Named("mapToEmail")
    default String mapToEmail(String emailOrUsername) {
        if (emailOrUsername != null && emailOrUsername.contains("@")) {
            return emailOrUsername;
        }
        return null;
    }

    // Custom method to map emailOrUsername to username if it does not contain "@"
    @Named("mapToUsername")
    default String mapToUsername(String emailOrUsername) {
        if (emailOrUsername != null && !emailOrUsername.contains("@")) {
            return emailOrUsername;
        }
        return null;
    }
}
