package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.dto.user.AuthenticationRequest;
import com.example.ecommerce.api.dto.user.AuthenticationResponse;
import com.example.ecommerce.api.dto.user.RegistrationRequest;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signUp")
    public ResponseEntity<Void> signUp(@Valid @RequestBody RegistrationRequest request){
        authenticationService.register(request);
        return ResponseEntity.created(URI.create("/api/v1/signIn")).build();
    }

    @PostMapping("/signIn")
    public ResponseEntity<AuthenticationResponse> signIn(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
