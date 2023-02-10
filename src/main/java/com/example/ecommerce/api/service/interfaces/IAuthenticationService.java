package com.example.ecommerce.api.service.interfaces;

import com.example.ecommerce.api.dto.user.AuthenticationRequestDto;
import com.example.ecommerce.api.dto.user.AuthenticationResponseDto;
import com.example.ecommerce.api.dto.user.RegistrationRequestDto;

public interface IAuthenticationService {

    void register(RegistrationRequestDto request);

    AuthenticationResponseDto authenticate(AuthenticationRequestDto request);
}
