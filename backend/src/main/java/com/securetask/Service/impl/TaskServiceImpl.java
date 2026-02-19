package com.securetask.Service.impl;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.securetask.DAO.TaskDAO;
import com.securetask.DAO.UserDAO;
import com.securetask.DTO.requests.CreateTaskRequest;
import com.securetask.DTO.requests.UpdateTaskRequest;
import com.securetask.DTO.responses.TaskResponse;
import com.securetask.Entitity.Task;
import com.securetask.Entitity.User;
import com.securetask.Exception.RessourceNotFoundException;
import com.securetask.Exception.BadRequestException;
import com.securetask.Mapper.TaskMapper;
import com.securetask.Service.TaskService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {


    private final TaskDAO taskDAO;
    private final UserDAO userDAO;
    private final TaskMapper taskMapper;

    // ==========================================
    // MANAGER METHODS
    // ==========================================

    @Override
    public List<TaskResponse> getAllTasksAssignedToUser(@NonNull Long userId) 
    {
        if (!userDAO.existsById(userId)) {
            throw new RessourceNotFoundException("User not found: " + userId);
        }   
        
        return taskDAO.findAllByAssigneeId(userId)
            .stream()
            .map(taskMapper::toResponse)
            .toList();
    }

    @Override
    public List<TaskResponse> getAllTasksCreatedByUser(@NonNull Long userId) 
    {
        if (!userDAO.existsById(userId)) {
            throw new RessourceNotFoundException("User not found: " + userId);
        }
        
        return taskDAO.findAllByCreatedById(userId)
            .stream()
            .map(taskMapper::toResponse)
            .toList();
    }

    // ==========================================
    // AUTH USER METHODS
    // ==========================================

    @Override
    public List<TaskResponse> getAllTasksAssignedToAuthUser() 
    {
        User currentUser = getCurrentUser();
        return taskDAO.findAllByAssigneeId(currentUser.getId())
            .stream()
            .map(taskMapper::toResponse)
            .toList();
    }

    @Override
    public List<TaskResponse> getAllTasksCreatedByAuthUser() 
    {
        User currentUser = getCurrentUser();
        return taskDAO.findAllByCreatedById(currentUser.getId())
            .stream()
            .map(taskMapper::toResponse)
            .toList();
    }

    // ==========================================
    // CRUD
    // ==========================================

    @Override
    public TaskResponse getTaskById(@NonNull Long taskId) 
    {
        Task task = findTaskByIdWithOwnershipCheck(taskId);
        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) 
    {
        User currentUser = getCurrentUser();

        User assignee = null;
        if (request.assigneeId() != null) {

            assignee = userDAO.findById(request.assigneeId())
                    .orElseThrow(() -> new RessourceNotFoundException("Assignee not found: " + request.assigneeId()));
        }

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(Task.StatusEnum.TODO)
                .assignee(assignee)
                .createdBy(currentUser)
                .build();

        return taskMapper.toResponse(taskDAO.save(task));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(@NonNull Long taskId, UpdateTaskRequest request) 
    {
        Task task = findTaskByIdWithOwnershipCheck(taskId);

        task = taskMapper.updateFromRequest(request, task);

        if (request.assigneeId() != null) {
            User newAssignee = userDAO.findById(request.assigneeId())
                .orElseThrow(() -> new RessourceNotFoundException("Assignee not found"));
            task.setAssignee(newAssignee);
        }

        if (request.status() != null) {
            validateStatusTransition(task.getStatus(), request.status());
            task.setStatus(request.status());
        }

        return taskMapper.toResponse(taskDAO.save(task));
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(@NonNull Long taskId, Task.StatusEnum newStatus) 
    {
        Task task = findTaskByIdWithOwnershipCheck(taskId);
        validateStatusTransition(task.getStatus(), newStatus);
        task.setStatus(newStatus);

        return taskMapper.toResponse(taskDAO.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(@NonNull Long taskId) 
    {
        findTaskByIdWithOwnershipCheck(taskId);
        taskDAO.deleteById(taskId);
    }

    

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    private User getCurrentUser() 
    {
        String email = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        
        return userDAO.findByEmail(email)
            .orElseThrow(() -> new RessourceNotFoundException("Authenticated user not found"));
    }

    private Task findTaskByIdWithOwnershipCheck(@NonNull Long taskId) 
    {
        Task task = taskDAO.findById(taskId)
            .orElseThrow(() -> new RessourceNotFoundException("Task not found: " + taskId));

        User currentUser = getCurrentUser();

        // Admin and Manager bypass ownership check
        boolean isAdminOrManager = currentUser.getRole() == User.Role.ADMIN 
            || currentUser.getRole() == User.Role.MANAGER;

        boolean isOwnerOrAssignee = task.getCreatedBy().getId().equals(currentUser.getId())
            || (task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId()));

        if (!isAdminOrManager && !isOwnerOrAssignee) {
            throw new AccessDeniedException("You don't have access to this task");
        }

        return task;
    }


    private void validateStatusTransition(Task.StatusEnum current, Task.StatusEnum next) 
    {
        boolean valid = switch (current) {
            case TODO -> next == Task.StatusEnum.IN_PROGRESS;
            case IN_PROGRESS -> next == Task.StatusEnum.DONE;
            case DONE -> false;  // Cannot transition from DONE
        };

        if (!valid) {
            throw new BadRequestException(
                "Invalid transition: " + current + " → " + next
            );
        }
    }

    
}
