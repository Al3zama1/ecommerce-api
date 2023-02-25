package com.example.ecommerce.api.mapstruct.mappers;

import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.mapstruct.dto.user.RegistrationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User mapRegistrationRequestDtoToUser(RegistrationRequestDto registrationRequestDto);
}
