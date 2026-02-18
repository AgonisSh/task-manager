package com.securetask.Authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securetask.DTO.requests.AuthRequest;
import com.securetask.DTO.requests.RegisterRequest;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Use application-test.yml for testing
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Reset DB before each test
public class AuthControllerMockMvcTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldRegisterUserSuccessfully() throws Exception 
    {
        RegisterRequest request = new RegisterRequest(
            "testUsername", "test@mail.com", "Test@1234", "Test@1234"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
            )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.username").value("testUsername"))
                .andExpect(jsonPath("$.expiresAt").exists());
    }


    @Test
    void shouldFailRegistrationDueToEmailValidation() throws Exception 
    {
        RegisterRequest request = new RegisterRequest(
            "test", "invalid-email", "CorrectPassword12!", "CorrectPassword12!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
            )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("email: Invalid email format"));
    }
    

    @Test 
    void shouldFailRegistrationDueToPasswordLengthValidation() throws Exception 
    {
        RegisterRequest request = new RegisterRequest(
            "test", "test@test.fr", "Weak1!", "Weak1!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
            )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("password: Password must be between 8 and 64 characters"));
               
    }

    @Test
    void shouldFailRegistrationDueToPasswordCharacterValidation() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "test", "test@test.fr", "WithoutSpecialCharacter123", "WithoutSpecialCharacter123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("password: Password must contain uppercase, lowercase, number, and special character"));

    }


    @Test
    void shouldFailRegistrationDueToPasswordMismatch() throws Exception 
    {
        RegisterRequest request = new RegisterRequest(
                "test", "test@test.fr", "CorrectPassword12!", "DifferentPassword12!");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(request)))
            )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }


    @Test
    void shouldFailRegistrationDueToDuplicateEmail() throws Exception
    {
        // First registration should succeed
        RegisterRequest firstRequest = new RegisterRequest(
            "testUser1", "test1@test.fr", "CorrectPassword12!", "CorrectPassword12!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(firstRequest)))
            )
                .andExpect(status().isCreated());

        // Second registration with same email should fail
        RegisterRequest secondRequest = new RegisterRequest(
            "testUser2", "test1@test.fr", "CorrectPassword12!", "CorrectPassword12!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(secondRequest)))
            )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already in use: test1@test.fr"));
    }


    @Test
    void shouldFailRegistrationDueToDuplicateUsername() throws Exception   
    {
        // First registration should succeed
        RegisterRequest firstRequest = new RegisterRequest(
            "testUser", "test1@test.fr", "CorrectPassword12!", "CorrectPassword12!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(firstRequest)))
            )
                .andExpect(status().isCreated());

        // Second registration with same username should fail
        RegisterRequest secondRequest = new RegisterRequest(
            "testUser", "test2@test.fr", "CorrectPassword12!", "CorrectPassword12!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(secondRequest)))
            )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already in use: testUser"));
    }


    @Test
    void shouldLoginWithValidCredentials() throws Exception 
    {
        // First register a user
        RegisterRequest registerRequest = new RegisterRequest(
            "testUser5", "test5@test.fr", "CorrectPassword12!", "CorrectPassword12!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(registerRequest)))
            )
                .andExpect(status().isCreated());

        // Now try to login with valid credentials
        AuthRequest loginRequest = new AuthRequest("test5@test.fr", "CorrectPassword12!");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(loginRequest)))
            )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.username").value("testUser5"))
                .andExpect(jsonPath("$.expiresAt").exists());
    }


    @Test
    void shouldFailLoginWithInvalidCredentials() throws Exception
    {
        // First register a user
        RegisterRequest registerRequest = new RegisterRequest(
            "testUser6", "test6@test.fr", "CorrectPassword12!", "CorrectPassword12!"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(registerRequest)))
            )
                .andExpect(status().isCreated());

        // Now try to login with invalid credentials
        AuthRequest loginRequest = new AuthRequest("test6@test.fr", "WrongPassword12!");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(Objects.requireNonNull(objectMapper.writeValueAsString(loginRequest)))
            )
                .andExpect(status().isUnauthorized());
    }
}


