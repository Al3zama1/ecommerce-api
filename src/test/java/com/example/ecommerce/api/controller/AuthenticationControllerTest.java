package com.example.ecommerce.api.controller;

import com.example.ecommerce.api.config.JwtService;
import com.example.ecommerce.api.config.SecurityConfig;
import com.example.ecommerce.api.config.WebSecurity;
import com.example.ecommerce.api.dto.user.AuthenticationRequest;
import com.example.ecommerce.api.dto.user.RegistrationRequest;
import com.example.ecommerce.api.repository.UserRepository;
import com.example.ecommerce.api.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import({WebSecurity.class, SecurityConfig.class, JwtService.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthenticationProvider authenticationProvider;
    @MockBean
    private AuthenticationService authenticationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtService jwtService;

    private static final String EMAIL = "john@gmail.com";
    private static final String PASSWORD = "12345678";

    @Test
    void ShouldRegisterUserWhenInputIsValid() throws Exception {
        // Given
        RegistrationRequest request = RegistrationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .verifyPassword(PASSWORD)
                .build();

        // When
        mockMvc.perform(post("/api/v1/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn();


        // Then
        ArgumentCaptor<RegistrationRequest> captor = ArgumentCaptor.forClass(RegistrationRequest.class);
        then(authenticationService).should(times(1)).register(captor.capture());
        assertThat(captor.getValue()).isEqualTo(request);
    }

    @Test
    void ShouldFailRegistrationWhenInputIsInvalid() throws Exception {
        // Given
        RegistrationRequest request = RegistrationRequest.builder()
                .email("john@gmail.com")
                .build();

        // When
        mockMvc.perform(post("/api/v1/signUp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Then
        then(authenticationService).shouldHaveNoInteractions();
    }

    @Test
    void ShouldReturnUserTokenWhenAuthenticationIsSuccessful() throws Exception {
        // Given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        // When
        mockMvc.perform(post("/api/v1/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then
        then(authenticationService).should(times(1)).authenticate(any());
    }

    @Test
    void ShouldFailAuthenticationWhenInputIsInvalid() throws Exception {
        // Given
        AuthenticationRequest request = AuthenticationRequest.builder()
                .password("lsjdlfjsdl")
                .build();

        // When
        mockMvc.perform(post("/api/v1/signIn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Then
        then(authenticationService).shouldHaveNoInteractions();
    }

}