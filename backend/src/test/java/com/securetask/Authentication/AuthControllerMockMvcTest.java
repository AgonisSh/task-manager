package com.securetask.Authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.securetask.DTO.requests.AuthRequest;
import com.securetask.DTO.requests.RefreshTokenRequest;
import com.securetask.DTO.requests.RegisterRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ==========================================
    // HELPERS
    // ==========================================

    @SuppressWarnings("null")
    private MvcResult register(String username, String email, String password, String confirmPassword) throws Exception 
    {
        RegisterRequest request = new RegisterRequest(username, email, password, confirmPassword);
        return mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn();
    }

    private MvcResult registerExpectCreated(String username, String email) throws Exception 
    {
        MvcResult result = register(username, email, "CorrectPassword12!", "CorrectPassword12!");
        assertEquals(201, result.getResponse().getStatus());
        return result;
    }

    private String registerAndGetRefreshToken(String username, String email) throws Exception 
    {
        MvcResult result = registerExpectCreated(username, email);
        return objectMapper.readTree(result.getResponse().getContentAsString())
            .get("refreshToken").asText();
    }

    @SuppressWarnings("null")
    private MvcResult login(String email, String password) throws Exception 
    {
        AuthRequest request = new AuthRequest(email, password);
        return mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
            .andReturn();
    }

    // ==========================================
    // REGISTER TESTS
    // ==========================================

    @Test
    @SuppressWarnings("null")
    void shouldRegisterUserSuccessfully() throws Exception 
    {
        RegisterRequest request = new RegisterRequest("testUsername", "test@mail.com", "Test@1234", "Test@1234");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.username").value("test@mail.com"))
            .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailRegistrationDueToEmailValidation() throws Exception 
    {
        RegisterRequest request = new RegisterRequest("test", "invalid-email", "CorrectPassword12!", "CorrectPassword12!");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("email: Invalid email format"));
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailRegistrationDueToPasswordLengthValidation() throws Exception 
    {
        RegisterRequest request = new RegisterRequest("test", "test@test.fr", "Weak1!", "Weak1!");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("password: Password must be between 8 and 64 characters"));
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailRegistrationDueToPasswordCharacterValidation() throws Exception 
    {
        RegisterRequest request = new RegisterRequest("test", "test@test.fr", "WithoutSpecialCharacter123", "WithoutSpecialCharacter123");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("password: Password must contain uppercase, lowercase, number, and special character"));
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailRegistrationDueToPasswordMismatch() throws Exception 
    {
        RegisterRequest request = new RegisterRequest("test", "test@test.fr", "CorrectPassword12!", "DifferentPassword12!");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailRegistrationDueToDuplicateEmail() throws Exception 
    {
        registerExpectCreated("testUser1", "test1@test.fr");

        RegisterRequest request = new RegisterRequest("testUser2", "test1@test.fr", "CorrectPassword12!", "CorrectPassword12!");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Email already in use: test1@test.fr"));
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailRegistrationDueToDuplicateUsername() throws Exception {
        registerExpectCreated("testUser", "test1@test.fr");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(
                    new RegisterRequest("testUser", "test2@test.fr", "CorrectPassword12!", "CorrectPassword12!"))))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Username already in use: testUser"));
    }

    // ==========================================
    // LOGIN TESTS
    // ==========================================

    @Test
    @SuppressWarnings("null")
    void shouldLoginWithValidCredentials() throws Exception {
        registerExpectCreated("testUser5", "test5@test.fr");

        AuthRequest loginRequest = new AuthRequest("test5@test.fr", "CorrectPassword12!");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.username").value("test5@test.fr"))
            .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        registerExpectCreated("testUser6", "test6@test.fr");

        AuthRequest loginRequest = new AuthRequest("test6@test.fr", "WrongPassword12!");
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequest))
            )
            .andExpect(status().isUnauthorized());
    }

    // ==========================================
    // PROTECTED ENDPOINT TESTS
    // ==========================================

    @Test
    void shouldFailToAccessProtectedEndpointWithoutToken() throws Exception 
    {
        mockMvc.perform(get("/api/v1/tasks"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailToAccessProtectedEndpointWithInvalidToken() throws Exception 
    {
        mockMvc.perform(get("/api/v1/tasks")
                .header("Authorization", "Bearer invalid.token.here"))
            .andExpect(status().isUnauthorized());
    }

    // ==========================================
    // REFRESH TOKEN TESTS
    // ==========================================

    @Test
    @SuppressWarnings("null")
    void shouldRefreshTokenWithValidRefreshToken() throws Exception 
    {
        String refreshToken = registerAndGetRefreshToken("testUser8", "test8@test.fr");

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
            .andExpect(jsonPath("$.username").value("test8@test.fr"))
            .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailToRefreshTokenWithExpiredRefreshToken() throws Exception 
    {
        registerExpectCreated("testUser9", "test9@test.fr");
        MvcResult loginResult = login("test9@test.fr", "CorrectPassword12!");

        String refreshToken = objectMapper.readTree(
            loginResult.getResponse().getContentAsString()
        ).get("refreshToken").asText();

        Thread.sleep(2000); // Wait for short-lived test token to expire

        mockMvc.perform(post("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken))))
            .andExpect(status().isUnauthorized());
    }
}
