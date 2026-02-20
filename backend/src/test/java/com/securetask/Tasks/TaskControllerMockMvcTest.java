package com.securetask.Tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.securetask.DTO.requests.CreateTaskRequest;
import com.securetask.DTO.requests.RegisterRequest;
import com.securetask.DTO.requests.TaskStatusUpdateRequest;
import com.securetask.DTO.requests.UpdateTaskRequest;
import com.securetask.Entitity.Task;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TaskControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // -----------------
    // HELPERS
    // -----------------
    @SuppressWarnings("null")
    private String registerAndGetToken(String username, String email, String password) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, email, password, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    @SuppressWarnings("null")
    private Long createTaskAndGetId(String token, String title, String description, Long assigneeId) throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(title, description, assigneeId);
        MvcResult result = mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return ((Number) JsonPath.read(result.getResponse().getContentAsString(), "$.id")).longValue();
    }

    // ==========================================
    // CREATE TASK TESTS
    // ==========================================

    @Test
    @SuppressWarnings("null")
    void shouldCreateTaskSuccessfully() throws Exception {
        String token = registerAndGetToken("user1", "user1@test.com", "SecurePass123!");

        CreateTaskRequest request = new CreateTaskRequest("Test Task", "Description", 1L);

        mockMvc.perform(post("/api/v1/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.description").value("Description"))
            .andExpect(jsonPath("$.status").value("TODO")) // Default status
            .andExpect(jsonPath("$.assigneeId").value(1))
            .andExpect(jsonPath("$.priority").doesNotExist());
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailCreateTaskWithoutToken() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("Test Task", "Description", 1L);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SuppressWarnings("null")
    void shouldFailCreateTaskWithBlankTitle() throws Exception {
        String token = registerAndGetToken("user2", "user2@test.com", "SecurePass123!");

        CreateTaskRequest request = new CreateTaskRequest(
                "", "Description", 1L);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @SuppressWarnings("null")
    void shouldFailCreateTaskWithNotFoundAssigneeId() throws Exception {
        String token = registerAndGetToken("user2", "user2@test.com", "SecurePass123!");

        CreateTaskRequest request = new CreateTaskRequest(
                "Test Task", "Description", 999L);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }


    @Test
    @SuppressWarnings("null")
    void shouldSuccessWithNullAssigneeId() throws Exception {
        String token = registerAndGetToken("user2", "user2@test.com", "SecurePass123!");

        CreateTaskRequest request = new CreateTaskRequest(
                "Test Task", "Description", null);
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.assigneeId").doesNotExist());
    }

    // ==========================================
    // GET TASK TESTS
    // ==========================================

    @Test
    void shouldGetOwnTaskById() throws Exception {
        String token = registerAndGetToken("user3", "user3@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(token, "My Task", "Task Description", null);

        mockMvc.perform(get("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("My Task"))
                .andExpect(jsonPath("$.createdByUserId").value(1));
    }

    @Test
    void shouldDenyAccessToOtherUserTask() throws Exception {
        String user1Token = registerAndGetToken("user1", "user1@test.com", "SecurePass123!");
        String user2Token = registerAndGetToken("user2", "user2@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(user1Token, "User1 Task", "Task Description", null);

        // User 2 tries to access User 1's task
        mockMvc.perform(get("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnNotFoundForNonExistentTask() throws Exception {
        String token = registerAndGetToken("user6", "user6@test.com", "SecurePass123!");

        mockMvc.perform(get("/api/v1/tasks/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAssignedTaskCreatedByOther() throws Exception 
    {
        String user1Token = registerAndGetToken("user1", "user1@test.com", "SecurePass123!");
        String user2Token = registerAndGetToken("user2", "user2@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(user1Token, "User1 Task", "Task Description", 2L);

        // User 2 should see the task assigned to him, which is created by User 1
        mockMvc.perform(get("/api/v1/tasks/assigned")
                .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(taskId))
                .andExpect(jsonPath("$[0].title").value("User1 Task"))
                .andExpect(jsonPath("$[0].assigneeId").value(2));
    }


    @Test
    void shouldDenyAccessToUnassignedTaskCreatedByOther() throws Exception 
    {
        String user1Token = registerAndGetToken("user1", "user1@test.com", "SecurePass123!");
        String user2Token = registerAndGetToken("user2", "user2@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(user1Token, "User1 Task", "Task Description", 1L);

        // User 2 shouldn't see the task assigned to User 1 and created by User 1
        mockMvc.perform(get("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden());
    }


    @Test
    void shouldGetAllTasksForUser() throws Exception 
    {
        String user1Token = registerAndGetToken("user1", "user1@test.com", "SecurePass123!");
        createTaskAndGetId(user1Token, "User1 Task 1", "Task Description 1", null);
        createTaskAndGetId(user1Token, "User1 Task 2", "Task Description 2", null);

        mockMvc.perform(get("/api/v1/tasks")
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].createdByUserId").value(1))
                .andExpect(jsonPath("$.size()").value(2));
    }


    @Test
    void shouldGetOnlyAssignedTasksForUser() throws Exception
    {
        String user1Token = registerAndGetToken("user1", "user1@test.com", "SecurePass123!");
        String user2Token = registerAndGetToken("user2", "user2@test.com", "SecurePass123!");

        createTaskAndGetId(user1Token, "User1 Task 1", "Task Description 1", 1L);
        createTaskAndGetId(user1Token, "User2 Task 1", "Task Description 1", 2L);
        createTaskAndGetId(user1Token, "User1 Task 2", "Task Description 2", null);

        mockMvc.perform(get("/api/v1/tasks/assigned")
                .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
        

    // ==========================================
    // UPDATE TASK TESTS
    // ==========================================

    @Test
    @SuppressWarnings("null")
    void shouldUpdateOwnTask() throws Exception {
        String token = registerAndGetToken("user8", "user8@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(token, "Original Title", "Original Description", null);

        // Add all missing fields 
        UpdateTaskRequest request = new UpdateTaskRequest(
                "Updated Title", "New Description", Task.StatusEnum.IN_PROGRESS,
                 Task.PriorityEnum.HIGH, LocalDateTime.now().plusDays(5), 1L);

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("New Description"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.dueDate").exists())
                .andExpect(jsonPath("$.assigneeId").value(1));
    }


    @Test
    @SuppressWarnings("null")
    void shouldDenyUpdateOnOtherUserTask() throws Exception {
        String user1Token = registerAndGetToken("user9", "user9@test.com", "SecurePass123!");
        String user2Token = registerAndGetToken("user10", "user10@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(user1Token, "User1 Task", "Task Description", null);

        UpdateTaskRequest request = new UpdateTaskRequest(
        "Hacked Title", null, Task.StatusEnum.DONE, Task.PriorityEnum.LOW, 
            LocalDateTime.now().plusDays(5), 1L);

        mockMvc.perform(put("/api/v1/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + user2Token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ==========================================
    // STATUS TRANSITION TESTS
    // ==========================================

    @Test
    @SuppressWarnings("null")
    void shouldAllowValidStatusTransition() throws Exception {
        String token = registerAndGetToken("user11", "user11@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(token, "Status Task", "Task Description", null);

        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(Task.StatusEnum.IN_PROGRESS);

        mockMvc.perform(patch("/api/v1/tasks/" + taskId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    @SuppressWarnings("null")
    void shouldRejectInvalidStatusTransition() throws Exception {
        String token = registerAndGetToken("user12", "user12@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(token, "Status Task", "Task Description", null);

        // Try jumping from TODO directly to DONE (invalid)
        TaskStatusUpdateRequest request = new TaskStatusUpdateRequest(Task.StatusEnum.DONE);

        mockMvc.perform(patch("/api/v1/tasks/" + taskId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ==========================================
    // DELETE TASK TESTS
    // ==========================================

    @Test
    void shouldDeleteOwnTask() throws Exception {
        String token = registerAndGetToken("user13", "user13@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(token, "Task to Delete", "Task Description", null);

        mockMvc.perform(delete("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDenyDeleteOnOtherUserTask() throws Exception {
        String user1Token = registerAndGetToken("user14", "user14@test.com", "SecurePass123!");
        String user2Token = registerAndGetToken("user15", "user15@test.com", "SecurePass123!");
        Long taskId = createTaskAndGetId(user1Token, "Protected Task", "Task Description", null);

        mockMvc.perform(delete("/api/v1/tasks/" + taskId)
                .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isForbidden());
    }
}
