package com.example.ecommerce.api;

import com.example.ecommerce.api.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionBodyResponseMatcher {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResultMatcher containsError(String expectedFieldName, String expectedMessage) {
        return mvcResult -> {
            String json = mvcResult.getResponse().getContentAsString();
            ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);
            List<ErrorResponse.ValidationError> fieldErrors = errorResponse.getErrors().stream()
                    .filter(fieldError -> fieldError.field().equals(expectedFieldName))
                    .filter(fieldError -> fieldError.message().equals(expectedMessage))
                    .collect(Collectors.toList());

            assertThat(fieldErrors).withFailMessage("Expecting exactly 1 error message with field name '%s' and message '%s'",
                            expectedFieldName, expectedMessage).hasSize(1);
        };
    }

    public static ExceptionBodyResponseMatcher exceptionMatcher() {
        return new ExceptionBodyResponseMatcher();
    }
}
