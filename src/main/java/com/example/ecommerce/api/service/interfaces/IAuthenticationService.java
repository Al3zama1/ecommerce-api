package com.example.ecommerce.api.service.interfaces;

import com.example.ecommerce.api.dto.user.AuthenticationRequest;
import com.example.ecommerce.api.dto.user.AuthenticationResponse;
import com.example.ecommerce.api.dto.user.RegistrationRequest;

public interface IAuthenticationService {

    void register(RegistrationRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request);
}
