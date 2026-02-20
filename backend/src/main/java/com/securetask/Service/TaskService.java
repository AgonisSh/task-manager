package com.securetask.Service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.securetask.DTO.requests.CreateTaskRequest;
import com.securetask.DTO.requests.UpdateTaskRequest;
import com.securetask.DTO.responses.TaskResponse;
import com.securetask.Entitity.Task;


public interface TaskService {

    // Manager methods
    List<TaskResponse> getAllTasksAssignedToUser(@NonNull Long userId);

    List<TaskResponse> getAllTasksCreatedByUser(@NonNull Long userId);

    // Auth user methods
    List<TaskResponse> getAllTasksAssignedToAuthUser();

    List<TaskResponse> getAllTasksCreatedByAuthUser();

    // CRUD
    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse getTaskById(@NonNull Long taskId);

    TaskResponse updateTask(@NonNull Long taskId, UpdateTaskRequest request);

    void deleteTask(@NonNull Long taskId);

    // Status
    TaskResponse updateTaskStatus(@NonNull Long taskId, Task.StatusEnum newStatus);
}
