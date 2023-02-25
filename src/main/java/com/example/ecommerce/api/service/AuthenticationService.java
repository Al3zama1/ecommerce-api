package com.example.ecommerce.api.service;

import com.example.ecommerce.api.config.JwtService;
import com.example.ecommerce.api.mapstruct.dto.user.AuthenticationRequestDto;
import com.example.ecommerce.api.mapstruct.dto.user.AuthenticationResponseDto;
import com.example.ecommerce.api.mapstruct.dto.user.RegistrationRequestDto;
import com.example.ecommerce.api.entity.User;
import com.example.ecommerce.api.entity.UserRole;
import com.example.ecommerce.api.exception.ConflictException;
import com.example.ecommerce.api.exception.ExceptionMessages;
import com.example.ecommerce.api.exception.InvalidCredentialsException;
import com.example.ecommerce.api.mapstruct.mappers.UserMapper;
import com.example.ecommerce.api.repository.UserRepository;
import com.example.ecommerce.api.service.interfaces.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService implements IAuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public void register(RegistrationRequestDto request) {
        // check if email is unique
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException(ExceptionMessages.USER_TAKEN);
        }

        // check that both password match
        if (!request.getPassword().equals(request.getVerifyPassword())) {
            throw new InvalidCredentialsException(ExceptionMessages.PASSWORDS_MUST_MATCH);
        }

        User user = userMapper.mapRegistrationRequestDtoToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);

        userRepository.save(user);
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {

        Optional<User> user = userRepository.findByEmail(request.getEmail());

        // Check that user with given email exists
        if (user.isEmpty()) throw new InvalidCredentialsException(ExceptionMessages.INVALID_CREDENTIALS);

        // Check that existing user and given password match
        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new InvalidCredentialsException(ExceptionMessages.INVALID_CREDENTIALS);
        }

        String jwtToken = jwtService.generateToken(user.get());

        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }
}
