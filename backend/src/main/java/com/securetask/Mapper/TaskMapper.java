package com.securetask.Mapper;

import org.springframework.stereotype.Component;

import com.securetask.DTO.requests.UpdateTaskRequest;
import com.securetask.DTO.responses.TaskResponse;
import com.securetask.Entitity.Task;

@Component
public class TaskMapper {
    

    public Task toEntity(TaskResponse response) {
       
        return Task.builder()
            .id(response.id())
            .title(response.title())
            .description(response.description())
            .status(Task.StatusEnum.valueOf(response.status()))
            .priority(Task.PriorityEnum.valueOf(response.priority()))
            .dueDate(response.dueDate())
            // Assignee and CreatedBy would need to be set separately, as they are references to User entities
            .build();
    }


    public Task updateFromRequest(UpdateTaskRequest request, Task task) 
    {
        if (request.title() != null) task.setTitle(request.title());
        if (request.description() != null) task.setDescription(request.description());
        if (request.priority() != null) task.setPriority(request.priority());
        if (request.dueDate() != null) task.setDueDate(request.dueDate());
        

        return task;
    }

    public TaskResponse toResponse(Task task) 
    {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus().toString(),
            task.getPriority() != null ? task.getPriority().toString() : null,
            task.getDueDate(),
            task.getAssignee() != null ? task.getAssignee().getId() : null,
            task.getCreatedBy().getId(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }


}
