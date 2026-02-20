package com.securetask.Repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.securetask.Entitity.Task;
import com.securetask.Entitity.User;


public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByAssigneeId(Long assigneeId);
    List<Task> findAllByCreatedById(Long createdById);
    List<Task> findByAssigneeAndStatus(User assignee, Task.StatusEnum status);
    
}
