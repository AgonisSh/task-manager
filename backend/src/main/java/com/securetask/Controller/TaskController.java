package com.securetask.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.securetask.DTO.requests.CreateTaskRequest;
import com.securetask.DTO.requests.TaskStatusUpdateRequest;
import com.securetask.DTO.requests.UpdateTaskRequest;
import com.securetask.DTO.responses.TaskResponse;
import com.securetask.Service.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks", description = "Task CRUD operations")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getMyTasks() 
    {
        return ResponseEntity.ok(taskService.getAllTasksCreatedByAuthUser());
    }

    @GetMapping("/assigned")
     public ResponseEntity<List<TaskResponse>> getMyAssignedTasks() 
    {
        return ResponseEntity.ok(taskService.getAllTasksAssignedToAuthUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getById(@PathVariable @NonNull Long id) 
    {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody CreateTaskRequest request) 
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(@PathVariable @NonNull Long id, @Valid @RequestBody UpdateTaskRequest request) 
    {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable @NonNull Long id, @RequestBody TaskStatusUpdateRequest request) 
    {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request.newStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) 
    {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    
}
