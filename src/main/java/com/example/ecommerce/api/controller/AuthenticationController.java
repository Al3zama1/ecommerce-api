package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.mapstruct.dto.user.AuthenticationRequestDto;
import com.example.ecommerce.api.mapstruct.dto.user.AuthenticationResponseDto;
import com.example.ecommerce.api.mapstruct.dto.user.RegistrationRequestDto;
import com.example.ecommerce.api.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signUp")
    public ResponseEntity<Void> signUp(@Valid @RequestBody RegistrationRequestDto request){
        authenticationService.register(request);
        return ResponseEntity.created(URI.create("/api/v1/signIn")).build();
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthenticationResponseDto> signIn(@Valid @RequestBody AuthenticationRequestDto request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
